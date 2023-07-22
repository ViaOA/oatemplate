<%@ include file="include/jspHeader.jspf"%>

<%

  formId = "htmlElement";
  form = oasession.getRequestForm(formId);
   
  oasession.setDebug(true); //qqqqqqq

  if (form == null) {
      form = oasession.createRequestForm(formId);

      HtmlElement comp = new HtmlElement("div");
      form.add(comp);

      // comp.addClass("oaTextAreaMessage");
      // comp.addStyle("padding", "122px 85px");
      // comp.addStyle("font-style", "italic");
      
      // comp.setFocus(true);
      // comp.setEnabled(false);
      // comp.setVisible(false);
      // comp.setFloatLabel("Float label for txt");
      // comp.setRequired(true);
      // comp.setAjaxSubmit(true);
      comp.setToolTip("tooltip text for txt component is here");
      // comp.setAjaxSubmit(true);
      comp.setOverflow(OAHtmlComponent.OverflowType.Auto);
      comp.setHeight("150px");
      comp.setWidth("150px");

      comp = new HtmlElement("span");
      comp.setInnerHtml("This is <b>HTML</b> text was replaced. <br><small>it worked!</small>");
      form.add(comp);
  
      OAHtmlComponent oaHtmlComp = comp.getOAComponent();
      if (oaHtmlComp == null) form.addError("getOAComponent is null");      
      
      InputCheckbox chk = new InputCheckbox("chkHidden") {
          public void onSubmitRunCommand(OAFormSubmitEvent formSubmitEvent) {
              form.
          }
      };
  
  
      for (String s : form.getErrorMessages()) {
          System.out.println("ERROR: "+s);
      }
  }
  
%>


<%@ include file="include/htmlHeader.jspf"%>

<style>
body {
    margin-left: 15px;
}
#div {
    border: solid thin;
}
</style>

<a href="oareset.jsp">OA Reset</a>

<H1><%=formId %></H1>


<div id="div">
<%= OAString.getDummyText(750, 500, 900) %>
</div>
<p>
<span id="span">This text/html should be replaced</span>
<p>

<fieldset>
    <legend>Options </legend>
<label>Hidden: <input id="chkHidden"></label><br>


<%@ include file="include/htmlFooter.jspf"%>


