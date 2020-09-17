<#import "./common.ftl" as com>
<html>
<@com.head />
<body>
<div class="pure-g" style="margin-top: 10%">
    <div class="pure-u-1-4">
        <@com.menu loggedIn=userSession??></@com.menu>
    </div>

    <div class="pure-u-3-4">
        <div class="heading-allowed">Allowed</div>
        <p>The principal has sufficient privileges to see this content.</p>
        <p>Restrictions for this URL: ${restriction}</p>
        <p>Current principal: ${userSession}</p>
    </div>
</div>
</body>
</html>