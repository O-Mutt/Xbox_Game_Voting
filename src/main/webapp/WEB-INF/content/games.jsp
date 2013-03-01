
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sj" uri="/struts-jquery-tags"%>
<html>
<head>
<sj:head jquerytheme="le-frog"/>
<title>Userery Xbox Game Voting</title>
<script type="text/javascript" src="http://masonry.desandro.com/jquery.masonry.min.js"></script>
<style type="text/css">
.block {
  padding: 10px;
}
.label {
  color: white;
}
</style>
<script>
jQuery(function($) {
  $(".blocks").masonry( {
    itemSelector: '.block',
    columnWidth: function(containerWidth) {
      return containerWidth / 3;
    }
  });
  //$.subscribe('reload', function(event, ui) {
    //$('#gametabs').tabs('select', event.originalEvent.ui.index);
    //$('.blocks').masonry().trigger('reload');
  //});
});
</script>
</head>
<body>

<%-- check if user is logged in --%>
<s:if test="#session.loggedin != 'true'">
  <jsp:forward page="login.jsp" />
</s:if>


<sj:tabbedpanel id="gametabs" animate="true" collapsible="true" useSelectedTabCookie="true">
  <sj:tab id="ownedTab" target="ownedGames" label="Owned Games" />
  <sj:tab id="unownedTab" target="unownedGames" label="Unowned Games" />
  <sj:tab id="addGameTab" target="addGame" label="Add Game" />
  <sj:tab id="markAsOwnedTab" target="markAsOwned" label="Mark Game As Owned"/>

  <sj:div id="ownedGames" cssClass="blocks">
    <h2 class="block" style="width: 100%;">Owned Xbox Games</h2>
      <s:iterator value="ownedGames" status="status">
        <sj:div cssClass="block">
          <s:url var="gameImageUrl" action="ajax/getImage">
            <s:param name="gameId"><s:property value="id"/></s:param>
          </s:url>
          <img src="<s:property value='%{gameImageUrl}' />"  style="height: 200px; width: 150px;"/><br/>
          <p style="width: 100%; text-align: center;"><s:property value="%{name}" /></p>
        </sj:div>
      </s:iterator>
  </sj:div>

  <sj:div id="unownedGames" cssClass="blocks">
    <s:actionerror cssClass="block" cssStyle="width: 100%;"/>
    <s:actionmessage cssClass="block" cssStyle="width: 100%;"/>
    <h2 class="block" style="width: 100%;">Unowned Xbox Games</h2>
    <s:iterator value="unownedGames" status="status"><!-- this will iterate over map of vote # -> game -->
      <s:set var="numberOfVotes"><s:property value="key"/></s:set>
      <s:iterator value="value" status="innerStatus">
        <s:url var="gameImageUrl" action="ajax/getImage">
          <s:param name="gameId"><s:property value="id"/></s:param>
        </s:url>
        <sj:div cssClass="block">
          <s:form id="voteForm_%{#status.index}_%{#innerStatus.index}" action="voteForGame" >
            <img src="<s:property value='%{gameImageUrl}' />"  style="height: 200px; width: 150px;"/> <br/>
            <p style="width: 100%; text-align: center;">Game: [<s:property value="name"/>] Votes: [<s:property value="numberOfVotes"/>]</p>
            <s:hidden name="voteGameId" value="%{id}" />
            <s:hidden name="voteGameName" value="%{name}" />
            <s:set var="submitLabel">Vote For <s:property value="name" /></s:set>
            <sj:submit value="%{submitLabel}" button="true" action="voteForGame" cssStyle="width: 100%;" />
          </s:form>
        </sj:div>
      </s:iterator>
    </s:iterator>
    <sj:submit cssClass="block" onclick="jQuery('#gametabs').tabs('select', 2); e.preventDefault();"
               button="true" value="Don\'t see your game? Add it here!" />
  </sj:div>

  <sj:div id="addGame">
    <s:actionerror cssClass="block" cssStyle="width: 100%;"/>
    <s:actionmessage cssClass="block" cssStyle="width: 100%;"/>
     <h2 class="block" style="width: 100%;">Add New Game</h2>
     <s:form id="addGame" action="addGame"  method="post" enctype="multipart/form-data">
       <sj:textfield id="addGameName" name="addGameName" label="Game Name" labelSeparator=":" />
       <sj:radio id="addGameOwned" name="addGameOwned" list="{'True', 'False'}" label="Is this game owned" labelSeparator=":"/>
       <s:file label="Game Image" labelSeparator=":" name="addGameImage" />
       <s:submit value="Add Game" button="true" cssClass="ui-button ui-widget ui-state-default ui-corner-all"/><!-- this is a bug in struts2-jquery, sj:submit fails to submit form (Issue 501 -->
     </s:form>
  </sj:div>

  <sj:div id="markAsOwned" cssClass="blocks">
    <s:actionerror cssClass="block" cssStyle="width: 100%;"/>
    <s:actionmessage cssClass="block" cssStyle="width: 100%;"/>
     <h2 class="block" style="width: 100%;">Mark Game As Owned</h2>
     <s:iterator value="unownedGames" status="status"><!-- this will iterate over map of vote # -> game -->
      <s:set var="numberOfVotes"><s:property value="key"/></s:set>
      <s:iterator value="value" status="innerStatus">
        <s:url var="gameImageUrl" action="ajax/getImage">
          <s:param name="gameId"><s:property value="id"/></s:param>
        </s:url>
        <sj:div cssClass="block">
          <s:form id="markAsOwned_%{#status.index}_%{#innerStatus.index}" action="markAsOwned" >
            <img src="<s:property value='%{gameImageUrl}' />"  style="height: 200px; width: 150px;"/><br/>
            <p style="width: 100%; text-align: center;">Game: [<s:property value="name"/>] Votes: [<s:property value="numberOfVotes"/>]</p>
            <s:hidden name="voteGameId" value="%{id}" />
            <s:hidden name="voteGameName" value="%{name}" />
            <s:set var="submitLabel">Mark <s:property value="name" /> as Owned</s:set>
            <sj:submit value="%{submitLabel}" button="true" action="markAsOwned"/>
          </s:form>
        </sj:div>
      </s:iterator>
    </s:iterator>
  </sj:div>
</sj:tabbedpanel>
<s:form action="logout">
  <sj:submit value="Logout" button="true" action="logout"/>
</s:form>
</body>
</html>