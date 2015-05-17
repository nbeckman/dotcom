<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>

<html>
<head><title>Add a new book</title>
</head>
<body>
<form name="addBookForm" action="/addBook" method="post">
Author: <input type="text" name="author"></input><br>
Title: <input type="text" name="title"></input><br>
<textarea cols="50" rows="4" name="description"></textarea><br>
<p><input type="submit" value="Submit" />
</form>
</body>
</html>