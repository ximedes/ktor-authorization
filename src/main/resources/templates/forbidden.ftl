<#import "./common.ftl" as com>
<html>
<@com.head />
<body>

<div class="pure-g" style="margin-top: 10%">
    <div class="pure-u-1-4">
        <@com.menu loggedIn=userSession??></@com.menu>
    </div>

    <div class="pure-u-3-4">
        <h1>Forbidden</h1>
        <p>The current user does not have sufficient privileges to view this page.</p>
        <p>${reason}</p>
    </div>
</div>
</body>
</html>