<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.nelsbeckman.books.PMF" %>
<%@ page import="com.nelsbeckman.books.BookRead" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.List" %>
<%@ page import="javax.jdo.PersistenceManager" %>
<%@ page import="javax.jdo.Query" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
	<head>
		<title>Books</title><LINK href="style.css" type="text/css" rel="stylesheet">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" /> 
		</head>
	<body bottomMargin="0" leftMargin="0" background="background.jpg" topMargin="0" rightMargin="0" marginwidth="0" marginheight="0">
		<table height="143" cellSpacing="0" cellPadding="0" width="780" border="0">
			<tr vAlign="top">
				<td width="780">
					<!----- Insert your logo below, or change templogotop.jpg to blanklogo.jpg if you do not know how to work with a graphics editor ------------------------------------------><IMG height="143" alt="" src="blanklogo.jpg" width="780" border="0">
					<!----------------------------------------------------------------------></td>
			</tr>
		</table>
 <%
    PersistenceManager pm = PMF.get().getPersistenceManager();
    // Test: Put in a book:
    // pm.makePersistent(new BookRead(new Date(), "Nels Beckman", "The Sun NEVER Rises", "<i>THIS BOOK RULES</i>"));
  
    // Do query
    Query query = pm.newQuery(BookRead.class);
    query.setOrdering("dateOfEntry desc");
    
    List<BookRead> books = new ArrayList<BookRead>();
    try {
      books = (List<BookRead>)query.execute();
    } finally {
      query.closeAll();
    }
  %>
		<table cellSpacing="0" cellPadding="0" width="100%" border="0">
			<tr vAlign="top">
				<td width="175">
					<table cellSpacing="0" cellPadding="4" width="175" border="0">
						<tr vAlign="top">
							<td width="175">
								<!------------------------ Menu section, links go below -----------------------------------------------------------------------------------------------------></td>
						</tr>
					</table>
				</td>
				<td width="510">
					<table cellSpacing="5" cellPadding="5" width="510" border="0">
						<tr vAlign="top">
							<td width="510">
								<!------------------------ Content zone, add your content below ---------------------------->
								<center>
									<h3>Books</h3>
								</center>
								<P align="center">
									<hr>
								<P></P>
								<P>This is a list of books (<%=books.size()%>!!!) that I have read since I started keeping track (March 2005) along with any commentary I feel like providing. It is mostly to augment my own memory, but I would be more that willing to answer questions regarding any of these books.<BR><BR></P>

<ul>

  <%
    // Iterate, and output each book.
    for( BookRead book : books ) {
      %>
      <li><b>"<%=book.getTitle()%>," <%=book.getAuthor()%></b> <%out.print(book.getHtmlDescription().getValue());%>
      <%
    }
  %>

  </ul>

									<BR>
									<BR>
									<BR>
									<BR>
									<BR>
									<BR>
									<BR>
									<BR>
									<BR>
									<BR>
									<BR>
									<BR>
								</P>
								<!------------------------------------------------------------------------------------------>
								<p align="right"><a href="http://www.steves-templates.com" target="_blank"></a></p>
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
<script type="text/javascript">
var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
</script>
<script type="text/javascript">
try {
var pageTracker = _gat._getTracker("UA-10862021-1");
pageTracker._trackPageview();
} catch(err) {}</script>
	</body>
</html>
