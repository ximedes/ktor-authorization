package com.ximedes

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import mu.KotlinLogging

typealias Role = String

class AuthorizationException(override val message: String) : Exception(message)

class RoleBasedAuthorization(config: Configuration) {
    private val logger = KotlinLogging.logger { }
    private val getRoles = config._getRoles

    class Configuration {
        internal var _getRoles: (Principal) -> Set<Role> = { emptySet() }

        fun getRoles(gr: (Principal) -> Set<Role>) {
            _getRoles = gr
        }

    }

    fun interceptPipeline(
        pipeline: ApplicationCallPipeline, any: Set<Role>? = null,
        all: Set<Role>? = null,
        none: Set<Role>? = null
    ) {
        pipeline.insertPhaseAfter(ApplicationCallPipeline.Features, Authentication.ChallengePhase)
        pipeline.insertPhaseAfter(Authentication.ChallengePhase, AuthorizationPhase)

        pipeline.intercept(AuthorizationPhase) {
            val principal =
                call.authentication.principal<Principal>() ?: throw AuthorizationException("Missing principal")
            val roles = getRoles(principal)
            val denyReasons = mutableListOf<String>()
            all?.let {
                val missing = all - roles
                if (missing.isNotEmpty()) {
                    denyReasons += "Principal ${principal} lacks required role(s) ${missing.joinToString(" and ")}"
                }
            }
            any?.let {
                if (any.none { it in roles }) {
                    denyReasons += "Principal ${principal} has none of the sufficient role(s) ${
                        any.joinToString(
                            " or "
                        )
                    }"
                }
            }
            none?.let {
                if (none.any { it in roles }) {
                    denyReasons += "Principal ${principal} has forbidden role(s) ${
                        (none.intersect(roles)).joinToString(
                            " and "
                        )
                    }"
                }
            }
            if (denyReasons.isNotEmpty()) {
                val message = denyReasons.joinToString(". ")
                logger.warn { "Authorization failed for ${call.request.path()}. ${message}" }
                throw AuthorizationException(message)
            }
        }
    }


    companion object Feature : ApplicationFeature<ApplicationCallPipeline, Configuration, RoleBasedAuthorization> {
        override val key = AttributeKey<RoleBasedAuthorization>("RoleBasedAuthorization")

        val AuthorizationPhase = PipelinePhase("Authorization")

        override fun install(
            pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit
        ): RoleBasedAuthorization {
            val configuration = Configuration().apply(configure)
            return RoleBasedAuthorization(configuration)
        }


    }
}

class AuthorisedRouteSelector(private val description: String) :
    RouteSelector(RouteSelectorEvaluation.qualityConstant) {
    override fun evaluate(context: RoutingResolveContext, segmentIndex: Int) = RouteSelectorEvaluation.Constant

    override fun toString(): String = "(authorize ${description})"
}

fun Route.withRole(role: Role, build: Route.() -> Unit) = authorizedRoute(all = setOf(role), build = build)

fun Route.withAllRoles(vararg roles: Role, build: Route.() -> Unit) =
    authorizedRoute(all = roles.toSet(), build = build)

fun Route.withAnyRole(vararg roles: Role, build: Route.() -> Unit) = authorizedRoute(any = roles.toSet(), build = build)

fun Route.withoutRoles(vararg roles: Role, build: Route.() -> Unit) =
    authorizedRoute(none = roles.toSet(), build = build)

private fun Route.authorizedRoute(
    any: Set<Role>? = null,
    all: Set<Role>? = null,
    none: Set<Role>? = null, build: Route.() -> Unit
): Route {

    val description = listOfNotNull(
        any?.let { "anyOf (${any.joinToString(" ")})" },
        all?.let { "allOf (${all.joinToString(" ")})" },
        none?.let { "noneOf (${none.joinToString(" ")})" }).joinToString(",")
    val authorisedRoute = createChild(AuthorisedRouteSelector(description))
    application.feature(RoleBasedAuthorization).interceptPipeline(authorisedRoute, any, all, none)
    authorisedRoute.build()
    return authorisedRoute
}

