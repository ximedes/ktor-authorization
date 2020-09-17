<#import "./common.ftl" as com>
<html>
<@com.head />
<body>

<div class="pure-g" style="margin-top: 10%">
    <div class="pure-u-1-4">
        <@com.menu loggedIn=userSession??></@com.menu>
    </div>

    <div class="pure-u-3-4">
        <div class="heading-allowed">Home</div>
        <p>This is the home page. It is available to all users, including not-logged in (anonymous) users</p>
    </div>
</div>
</body>
</html>