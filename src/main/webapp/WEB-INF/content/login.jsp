<!DOCTYPE HTML>
<html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sj" uri="/struts-jquery-tags"%>
<head>
  <sj:head jquerytheme="le-frog"/>
  <title>User Login</title>
</head>

<body>
  <sj:div id="main" >
    <h2>User Sign in</h2>
    <s:form action="login" >
       <s:actionerror/>
       <s:actionmessage/>
       <sj:textfield name="username" label="Username" required="true" requiredposition="right"/>
       <s:password name="password" label="Password" required="true" requiredposition="right"/>

        *All fields stored as plain text*<br/>
       **If user doesn't exist, it will be automatically created**
       <sj:submit button="true" validate="true" value="Login" />
    </s:form>
  </sj:div>
</body>
</html>