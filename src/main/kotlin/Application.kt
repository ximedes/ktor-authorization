package com.ximedes

import freemarker.cache.ClassTemplateLoader
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.freemarker.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.sessions.*
import mu.KotlinLogging

fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

data class UserSession(val name: String, val roles: Set<String> = emptySet()) : Principal
data class OriginalRequestURI(val uri: String)

@Suppress("unused")
fun Application.module() {

    val logger = KotlinLogging.logger {}

    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }

    install(StatusPages) {
        exception<AuthorizationException> {
            call.response.status(HttpStatusCode.Forbidden)
            call.respond(
                FreeMarkerContent(
                    "forbidden.ftl",
                    mapOf("userSession" to call.sessions.get<UserSession>(), "reason" to it.message)
                )
            )
        }
    }

    install(Sessions) {
        cookie<UserSession>("ktor_session_cookie", SessionStorageMemory())
        cookie<OriginalRequestURI>("original_request_cookie")
    }

    install(Authentication) {

        session<UserSession> {
            challenge {
                logger.info { "No valid session found for this route, redirecting to login form" }
                call.sessions.set(OriginalRequestURI(call.request.uri))
                call.respondRedirect("/login")
            }
            validate { session: UserSession ->
                logger.info { "User ${session.name} logged in by existing session" }
                session
            }
        }

    }

    install(RoleBasedAuthorization) {
        getRoles { (it as UserSession).roles }
    }

    routing {

        get("/") { call.showContent("home.ftl") }
        get("/login") { call.showContent("login.ftl") }

        post("/login") {
            val params = call.receiveParameters()
            val username = params["username"]
            val password = params["password"]
            val roles = params.getAll("roles")?.toSet() ?: emptySet()
            if (username != null && password == "secret") {
                call.sessions.set(UserSession(username, roles))
                val redirectURL = call.sessions.get<OriginalRequestURI>()?.also {
                    call.sessions.clear<OriginalRequestURI>()
                }
                call.respondRedirect(redirectURL?.uri ?: "/")
            } else {
                logger.warn { "Failed login attempt for user `$username` with roles `${roles.joinToString(",")}` who didn't use the right password which is `secret`" }
                call.respondRedirect("/login")
            }
        }

        get("/logout") {
            call.sessions.clear<UserSession>()
            call.respondRedirect("/")
        }

        authenticate {
            get("/login-required") {
                call.showProtectedContent("protected.ftl", "Principal must exist")
            }
            withRole("ABC") {
                get("/role-abc-required") {
                    call.showProtectedContent("protected.ftl", "Principal must have role ABC")
                }
            }
            withAllRoles("ABC", "DEF") {
                get("/roles-abc-def-required") {
                    call.showProtectedContent("protected.ftl", "Principal must have roles ABC and DEF")
                }
            }
            withAnyRole("DEF", "GHI") {
                get("/roles-any-from-def-ghi-required") {
                    call.showProtectedContent(
                        "protected.ftl", "Principal must have role DEF and/or GHI"
                    )
                }
            }
            withoutRoles("ABC", "GHI") {
                get("/roles-none-of-abc-ghi-allowed") {
                    call.showProtectedContent(
                        "protected.ftl", "Principal must NOT have roles ABC or GHI"
                    )
                }
            }
            withRole("ABC") {
                withoutRoles("GHI") {
                    get("/role-abc-required-ghi-forbidden") {
                        call.showProtectedContent(
                            "protected.ftl", "Principal must have role ABC and must NOT have role GHI"
                        )
                    }
                }
            }
        }


        static("static") {
            resources("static")
        }
    }

}

suspend fun ApplicationCall.showContent(template: String) = respond(
    FreeMarkerContent(
        template,
        mapOf("userSession" to sessions.get<UserSession>())
    )
)


suspend fun ApplicationCall.showProtectedContent(template: String, restriction: String) = respond(
    FreeMarkerContent(
        template,
        mapOf("userSession" to sessions.get<UserSession>(), "restriction" to restriction)
    )
)

