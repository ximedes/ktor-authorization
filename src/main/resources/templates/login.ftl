<#import "./common.ftl" as com>
<html>
<@com.head />
<body>
<div class="pure-g" style="margin-top: 10%">
    <div class="pure-u-1-4">
        <@com.menu loggedIn=userSession??></@com.menu>
    </div>

    <div class="pure-u-3-4">
        <h1>Login required</h1>
        <p>Choose any usernames and select your desired roles.The password is always <code>secret</code></p>
        <br/>
        <form action="/login" method="post" class="pure-form pure-form-aligned">
            <fieldset>
                <div class="pure-control-group">
                    <label for="username">Username:</label>
                    <input type="text" id="username" name="username" autofocus/>
                </div>
                <div class="pure-control-group">
                    <label for="password">Password:</label>
                    <input type="password" id="password" name="password"/>
                </div>
                <div class="pure-control-group">
                    <label for="roles">Roles:</label>
                    <input type="checkbox" id="role-abc" name="roles" value="ABC"/> <span>ABC</span>
                    <input type="checkbox" id="role-def" name="roles" value="DEF"/> <span>DEF</span>
                    <input type="checkbox" id="role-ghi" name="roles" value="GHI"/> <span>GHI</span>
                </div>
            </fieldset>
            <div class="pure-controls">
                <button type="submit" class="pure-button button-login">Submit</button>
            </div>
        </form>
    </div>
</div>
</body>
</html>