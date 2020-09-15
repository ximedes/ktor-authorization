<#macro menu loggedIn>
    <div class="pure-menu">
        <ul class="pure-menu-list">
            <li class="pure-menu-item">
                <a href="/" class="pure-menu-link">Home</a>
            </li>

            <li class="pure-menu-item">
                <a href="/login-required" class="pure-menu-link">Login required</a>
            </li>
            <li class="pure-menu-item">
                <a href="/role-abc-required" class="pure-menu-link">Role ABC required</a>
            </li>
            <li class="pure-menu-item">
                <a href="/roles-abc-def-required" class="pure-menu-link">Roles ABC and DEF required</a>
            </li>
            <li class="pure-menu-item">
                <a href="/roles-any-from-def-ghi-required" class="pure-menu-link">Role DEF or GHI required</a>
            </li>
            <li class="pure-menu-item">
                <a href="/roles-none-of-abc-ghi-allowed" class="pure-menu-link">Roles ABC and GHI forbidden</a>
            </li>
            <li class="pure-menu-item">
                <a href="/role-abc-required-ghi-forbidden" class="pure-menu-link">Role ABC required, GHI forbidden</a>
            </li>
            <#if loggedIn>
                <a href="/logout" class="pure-menu-link pure-button button-menu button-logout">Logout</a>
            <#else>
                <a href="/login" class="pure-menu-link pure-button button-menu button-login">Login</a>
            </#if>

        </ul>
    </div>
    <script>
        const menuItems = document.getElementsByClassName("pure-menu-link");
        for (i = 0; i < menuItems.length; i++) {
            if (!menuItems[i].classList.contains("pure-button") && menuItems[i].href === document.location.href) {
                menuItems[i].classList.add("pure-menu-item-current")
            }
        }
    </script>
</#macro>

<#macro head>
    <head>
        <link rel="stylesheet" href="/static/purecss/pure-min-2.0.3.css"
              integrity="sha384-cg6SkqEOCV1NbJoCu11+bm0NvBRc8IYLRGXkmNrqUBfTjmMYwNKPWBTIKyw9mHNJ">
        <link rel="stylesheet" href="/static/ktor-auth.css"/>
        <title>Ktor auth example</title>
    </head>
</#macro>