<?xml version="1.0" encoding="UTF-8"?>


<!--
  The MIT License (MIT)
  
  Copyright (c) 2010 Technische Universitaet Berlin
  
  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:
  
  The above copyright notice and this permission notice shall be included in
  all copies or substantial portions of the Software.
  
  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
  THE SOFTWARE.
-->

<!--
  Authors:  Tilman Rassy, Fritz Lehmann-Grube

  $Id: build_sitemap.xmap.tpl,v 1.5 2008/03/10 16:02:52 grudzin Exp $

-->

<map:sitemap xmlns:map="http://apache.org/cocoon/sitemap/1.0">

<!--
   ==========================================================================================
   Components
   ==========================================================================================
-->

<map:components>

  <!-- Generators -->

  <map:generators default="document">

    <map:generator name="authentication"
                   src="net.mumie.cocoon.generators.AuthenticationGenerator"/>

    <map:generator name="document" src="net.mumie.cocoon.generators.DocumentGenerator"/>

    <map:generator name="pseudo-document"
                   src="net.mumie.cocoon.generators.PseudoDocumentGenerator"/>

    <map:generator name="checkin"
                   src="net.mumie.cocoon.generators.CheckinGenerator">
      <delete-uploads>false</delete-uploads>
    </map:generator>

    <map:generator name="upload"
                   src="net.mumie.cocoon.generators.UploadedFileGenerator"/>

  </map:generators>

  <!-- Readers -->

  <map:readers default="document">

    <map:reader name="document"
                src="net.mumie.cocoon.readers.DocumentReader"/>

    <map:reader name="string"
                src="net.mumie.cocoon.readers.StringReader"/>

    <map:reader name="error"
                src="net.mumie.cocoon.readers.ErrorReader"/>

    <map:reader name="resource"
                src="org.apache.cocoon.reading.ResourceReader"/>

  </map:readers>

  <!-- Transformers -->

  <map:transformers default="xslt">

    <map:transformer name="xslt"
                     src="org.apache.cocoon.transformation.TraxTransformer"/>

    <map:transformer name="sitemap"
                     src="net.mumie.cocoon.transformers.SitemapTransformer">
      <url-prefix value="@url-prefix@"/>
    </map:transformer>

  </map:transformers>

  <!-- Serializers -->
  
  <map:serializers default="xml">

    <map:serializer name="xml" src="org.apache.cocoon.serialization.XMLSerializer"
                    mime-type="text/xml">
      <encoding>ASCII</encoding>
    </map:serializer>

    <map:serializer name="text"
                    src="org.apache.cocoon.serialization.TextSerializer"
                    mime-type="text/plain"/>

  </map:serializers>

  <!-- Actions -->

  <map:actions>

    <map:action name="login"
                src="org.apache.cocoon.webapps.authentication.acting.LoginAction"/>

    <map:action name="logout"
                src="org.apache.cocoon.webapps.authentication.acting.LogoutAction"/>

    <map:action name="check-if-logged-in"
                src="org.apache.cocoon.webapps.authentication.acting.LoggedInAction"/>

    <map:action name="protect"
                src="org.apache.cocoon.webapps.authentication.acting.AuthAction"/>

    <map:action name="add-response-headers"
                src="net.mumie.cocoon.actions.AddResponseHeadersAction"/>

    <map:action name="check-user-groups"
                src="net.mumie.cocoon.actions.CheckUserGroupsAction"/>

  </map:actions>

  <!-- Matchers -->
  
  <map:matchers default="wildcard">

    <map:matcher name="wildcard"
                 src="org.apache.cocoon.matching.WildcardURIMatcher"/>

  </map:matchers>

  <!-- Selectors -->

  <map:selectors default="browser">

    <map:selector name="browser"
                  src="org.apache.cocoon.selection.BrowserSelector">
      <browser name="japs-client" useragent="JapsClient"/>
    </map:selector>

    <map:selector name="simple"
                  src="org.apache.cocoon.selection.SimpleSelector"/>

  </map:selectors>

  <!-- Pipes -->

  <map:pipes default="non-caching">

    <map:pipe name="caching"
              src="org.apache.cocoon.components.pipeline.impl.CachingProcessingPipeline"/>

    <map:pipe name="non-caching"
              src="org.apache.cocoon.components.pipeline.impl.NonCachingProcessingPipeline"/>

  </map:pipes>

</map:components>

<!--
   ==========================================================================================
   Pipelines
   ==========================================================================================
-->

<map:pipelines>

  <!-- Authentication manager configuration -->

  <map:component-configurations>
    <authentication-manager>
      <handlers>
        <handler name="user">
	  <redirect-to uri="cocoon://public/auth/login-request"/>
  	  <authentication uri="cocoon:raw://internal/auth/auth"/>
        </handler>
      </handlers>
    </authentication-manager>
  </map:component-configurations>

  <!-- Internal URL (authentication) -->

  <map:pipeline internal-only="true">
    <map:match pattern="internal/auth/auth">
      <map:generate type="authentication"/>
      <map:serialize type="xml"/>
    </map:match>
  </map:pipeline>

  <!-- Public URLs (login) -->
  
  <map:pipeline>

    <!-- Asks the user for login with name and password -->
    <map:match pattern="public/auth/login-request">
      <map:act type="add-response-headers">
        <map:parameter name="X-Mumie-Login-Required" value="yes"/>
        <map:read type="string">
          <map:parameter name="string" value="Login required"/>
        </map:read>
      </map:act>
    </map:match>

    <!-- Does the actual login -->
    <map:match pattern="public/auth/login">
      <map:act type="check-if-logged-in">
        <map:parameter value="user" name="handler"/>
        <!-- TODO: Handle this case (already logged in) -->
      </map:act>
      <map:act type="login">
        <map:parameter value="user" name="handler"/>
        <map:parameter name="parameter_login-name" value="{request-param:login-name}"/>
        <map:parameter name="parameter_password" value="{request-param:password}"/>
        <map:parameter name="parameter_resource" value="{request-param:resource}"/>
        <map:redirect-to uri="{request-param:resource}"/>
      </map:act>
    </map:match>

    <!-- Logout -->
    <map:match pattern="public/auth/logout">
      <map:act type="check-if-logged-in">
        <map:parameter name="handler" value="user"/>
        <map:act type="logout">
          <map:parameter name="handler" value="user"/>
          <map:read type="string">
            <map:parameter name="string" value="Logout successful"/>
          </map:read>
        </map:act>
        <map:act type="add-response-headers">
          <map:parameter name="X-Mumie-Status" value="ERROR"/>
          <map:parameter name="X-Mumie-Error" value="Logout failed"/>
          <map:read type="string">
            <map:parameter name="string" value="Logout failed"/>
          </map:read>
        </map:act>
      </map:act>
      <map:read type="string">
        <map:parameter name="string" value="Not logged-in"/>
      </map:read>
    </map:match>

  </map:pipeline>

  <!-- Protected URLs -->
  
  <map:pipeline>

    <map:match pattern="protected/**">
      <map:act type="protect">
        <map:parameter value="user" name="handler"/>
        
        <!-- User group check, access for admins only -->
        <map:act type="check-user-groups">
          <map:parameter name="user-groups" value="0"/>

          <!-- Response if login was successful -->
          <map:match pattern="protected/auth/login-successful">
            <map:read type="string">
              <map:parameter name="string" value="Login successful"/>
            </map:read>
          </map:match>

          <!-- Sitemap creation -->
          <map:match pattern="protected/admin/create-sitemap">
	    <map:generate type="upload"/>
            <map:transform type="sitemap"/>
	    <map:serialize type="xml"/>
          </map:match>

          <!-- Checkin -->
          <map:match pattern="protected/checkin/checkin">
	    <map:generate type="checkin"/>
            <map:transform src="fs_content/checkin_response.xsl"/>
	    <map:serialize type="text"/>
          </map:match>

        </map:act> <!-- End of check-user-groups action -->
      </map:act> <!-- End of protect action -->
    </map:match> <!-- End of "protected/**" matcher -->

    <!-- Error handling -->
    <map:handle-errors>
      <map:read type="error"/>
    </map:handle-errors>

  </map:pipeline>

</map:pipelines>

</map:sitemap>
