<?xml version="1.0"?>


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
  Authors: Tilman Rassy <rassy@math.tu-berlin.de>
           Fritz Lehmann-Grube <lehmannf@math.tu-berlin.de>

  $Id: sitemap.xmap.src.tpl,v 1.205 2010/01/12 13:47:47 linges Exp $
-->

<map:sitemap xmlns:map="http://apache.org/cocoon/sitemap/1.0"
             xmlns:smac="http://www.mumie.net/xml-namespace/sitemap-autocoding">

<map:components>

<!-- ==================================================================================== -->
<!-- h1: Generators                                                                       -->
<!-- ==================================================================================== -->

  <map:generators default="document">

    <map:generator name="document"
                   src="net.mumie.cocoon.generators.DocumentGenerator"/>

    <map:generator name="pseudo-document"
                   src="net.mumie.cocoon.generators.PseudoDocumentGenerator"/>

    <map:generator name="document-index"
                   src="net.mumie.cocoon.generators.DocumentIndexGenerator"/>

    <map:generator name="pseudo-document-index"
                   src="net.mumie.cocoon.generators.PseudoDocumentIndexGenerator"/>

    <map:generator name="classes-and-courses-index"
                   src="net.mumie.cocoon.generators.ClassesAndCoursesIndexGenerator "/>

    <map:generator name="user"
                   src="net.mumie.cocoon.generators.UserGenerator"/>

    <map:generator name="authentication"
                   src="net.mumie.cocoon.generators.AuthenticationGenerator"/>

    <map:generator name="checkin" src="net.mumie.cocoon.generators.CheckinGenerator"/>

    <map:generator name="create-problem-wrapper" src="net.mumie.cocoon.generators.CreateProblemWrapperGenerator"/>

    <map:generator name="session-list"
                   src="net.mumie.cocoon.generators.SessionListGenerator"/>

    <map:generator name="service-status"
                   src="net.mumie.cocoon.generators.ServiceStatusGenerator"/>

    <map:generator name="service-status-overview"
                   src="net.mumie.cocoon.generators.ServiceStatusOverviewGenerator"/>

    <map:generator name="problem-datasheet"
                   src="net.mumie.cocoon.generators.ProblemDataSheetGenerator"/>

    <map:generator name="file"
                   src="org.apache.cocoon.generation.FileGenerator"/>

    <map:generator name="upload"
                   src="net.mumie.cocoon.generators.UploadedFileGenerator"/>

    <map:generator name="worksheet-bulk-correction" src="net.mumie.cocoon.generators.WorksheetBulkCorrectionGenerator"/>

    <map:generator name="tutorial-user-problem-grades"
                   src="net.mumie.cocoon.generators.TutorialUserProblemGradesGenerator"/>

    <map:generator name="tutorial-user-traditional-problem-grades"
                   src="net.mumie.cocoon.generators.TutorialUserTraditionalProblemGradesGenerator"/>

    <map:generator name="user-problem-grades"
                   src="net.mumie.cocoon.generators.UserProblemGradesGenerator"/>

    <map:generator name="worksheet-user-problem-grades"
                   src="net.mumie.cocoon.generators.WorksheetUserProblemGradesGenerator"/>

    <map:generator name="courses-and-tutorials-index"
                   src="net.mumie.cocoon.generators.CoursesAndTutorialsIndexGenerator"/>

    <map:generator name="class-members-index"
                   src="net.mumie.cocoon.generators.ClassMembersIndexGenerator"/>

    <map:generator name="total-class-grades"
                   src="net.mumie.cocoon.generators.TotalClassGradesGenerator"/>

    <map:generator name="class-user-worksheet-grades"
                   src="net.mumie.cocoon.generators.ClassUserWorksheetGradesGenerator"/>
    
    <map:generator name="class-user-problem-grades" 
                   src="net.mumie.cocoon.generators.ClassUserProblemGradesGenerator"/>
    

    <map:generator name="void-user"
                   src="net.mumie.cocoon.generators.VoidUserGenerator"/>

    <map:generator name="void-pseudo-document"
                   src="net.mumie.cocoon.generators.VoidUserGenerator"/>

    <map:generator name="search-users"
                   src="net.mumie.cocoon.generators.SearchUsersGenerator"/>

  </map:generators>

<!-- ==================================================================================== -->
<!-- h1: Readers                                                                          -->
<!-- ==================================================================================== -->

  <map:readers default="document">

    <map:reader name="document"
                src="net.mumie.cocoon.readers.DocumentReader"/>

    <map:reader name="string"
                src="net.mumie.cocoon.readers.StringReader"/>

    <map:reader name="resource"
                src="org.apache.cocoon.reading.ResourceReader"
                pool-max="32"/>

    <map:reader name="service-status"
                src="net.mumie.cocoon.readers.ServiceStatusReader"/>

    <map:reader name="store-problem-answers"
                src="net.mumie.cocoon.readers.StoreProblemAnswersReader"/>

    <map:reader name="session-count"
                src="net.mumie.cocoon.readers.SessionCountReader"/>

    <map:reader name="error" src="net.mumie.cocoon.readers.ErrorReader"/>

  </map:readers>

<!-- ==================================================================================== -->
<!-- h1: Transformers                                                                     -->
<!-- ==================================================================================== -->

  <map:transformers default="xslt">

    <map:transformer name="xslt"
                     src="org.apache.cocoon.transformation.TraxTransformer"/>

    <map:transformer name="add-user"
                     src="net.mumie.cocoon.transformers.AddUserTransformer"/>

    <map:transformer name="add-pseudo-document"
                     src="net.mumie.cocoon.transformers.AddPseudoDocumentTransformer"/>

    <map:transformer name="add-request-params"
                     src="net.mumie.cocoon.transformers.AddRequestParamsTransformer"/>

    <map:transformer name="add-params"
                     src="net.mumie.cocoon.transformers.AddParamsTransformer"/>

    <map:transformer name="add-current-time"
                     src="net.mumie.cocoon.transformers.AddCurrentTimeTransformer"/>

    <map:transformer name="resolve-generic-refs"
                     src="net.mumie.cocoon.transformers.ResolveGenericRefsTransformer"/>

    <map:transformer name="resolve-generic-section-entries"
                     src="net.mumie.cocoon.transformers.ResolveGenericSectionEntriesTransformer"/>

    <map:transformer name="add-problem-datasheet"
                     src="net.mumie.cocoon.transformers.AddProblemDataSheetTransformer"/>

    <map:transformer name="add-reference"
                     src="net.mumie.cocoon.transformers.AddReferenceTransformer"/>

    <map:transformer name="add-worksheet-user-problem-grades"
                     src="net.mumie.cocoon.transformers.AddWorksheetUserProblemGradesTransformer"/>

    <map:transformer name="worksheet-user-bulk-correction"
                     src="net.mumie.cocoon.transformers.WorksheetUserBulkCorrectionTransformer"/>

    <map:transformer name="set-html-form-data"
                     src="net.mumie.cocoon.transformers.SetHTMLFormDataTransformer"/>

    <map:transformer name="add-pseudodoc-index"
                     src="net.mumie.cocoon.transformers.AddPseudoDocumentIndexTransformer"/>

    <map:transformer name="sitemap"
                     src="net.mumie.cocoon.transformers.SitemapTransformer">
      <url-prefix value="@url-prefix@"/>
    </map:transformer>
 
  </map:transformers>

<!-- ==================================================================================== -->
<!-- h1: Serializers                                                                      -->
<!-- ==================================================================================== -->
  
  <map:serializers default="xhtml">

    <map:serializer name="xhtml"
                    src="org.apache.cocoon.serialization.XMLSerializer"
                    mime-type="text/xml; charset=utf-8">
      <doctype-public>-//W3C//DTD XHTML 1.0 Strict//EN</doctype-public>
      <doctype-system>http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd</doctype-system>
      <encoding>ASCII</encoding>
    </map:serializer>

    <map:serializer name="xml"
                    src="org.apache.cocoon.serialization.XMLSerializer"
                    mime-type="text/xml">
      <encoding>ASCII</encoding>
    </map:serializer>

    <map:serializer name="css"
                    src="org.apache.cocoon.serialization.TextSerializer"
                    mime-type="text/css"/>

    <map:serializer name="javascript"
                    src="org.apache.cocoon.serialization.TextSerializer"
                    mime-type="application/x-javascript"/>

    <map:serializer name="text"
                    src="org.apache.cocoon.serialization.TextSerializer"
                    mime-type="text/plain"/>
    
    <map:serializer name="csv" src="org.apache.cocoon.serialization.TextSerializer" mime-type="text/csv">
      <encoding>ISO-8859-1</encoding>
    </map:serializer>

  </map:serializers>

<!-- ==================================================================================== -->
<!-- h1: Actions                                                                          -->
<!-- ==================================================================================== -->
  
  <map:actions>

    <map:action name="login"
                src="org.apache.cocoon.webapps.authentication.acting.LoginAction"/>

    <map:action name="logout"
                src="org.apache.cocoon.webapps.authentication.acting.LogoutAction"/>

    <map:action name="check-if-logged-in"
                src="org.apache.cocoon.webapps.authentication.acting.LoggedInAction"/>

    <map:action name="protect"
                src="org.apache.cocoon.webapps.authentication.acting.AuthAction"/>

    <map:action name="resolve-generic-document"
                src="net.mumie.cocoon.actions.ResolveGenericDocumentAction"/>

    <map:action name="resolve-vc-thread"
                src="net.mumie.cocoon.actions.ResolveVCThreadAction"/>

    <map:action name="add-response-headers"
                src="net.mumie.cocoon.actions.AddResponseHeadersAction"/>

    <map:action name="clear-cache"
                src="org.apache.cocoon.acting.ClearCacheAction"/>

    <map:action name="clear-persistent-store"
                src="org.apache.cocoon.acting.ClearPersistentStoreAction"/>

    <map:action name="check-pseudodoc-read-permission"
                src="net.mumie.cocoon.actions.CheckPseudoDocReadPermissionAction"/>

    <map:action name="check-read-permission"
                src="net.mumie.cocoon.actions.CheckReadPermissionAction"/>

    <map:action name="check-user-groups"
                src="net.mumie.cocoon.actions.CheckUserGroupsAction"/>

    <map:action name="provide-worksheet-infos"
                src="net.mumie.cocoon.actions.ProvideWorksheetInfosAction"/>

    <map:action name="provide-course-section-infos"
                src="net.mumie.cocoon.actions.ProvideCourseSectionInfosAction"/>

    <map:action name="provide-selftest-infos"
                src="net.mumie.cocoon.actions.ProvideSelftestInfosAction"/>

    <map:action name="provide-course-infos"
                src="net.mumie.cocoon.actions.ProvideCourseInfosAction"/>

    <map:action name="provide-parent-course"
                src="net.mumie.cocoon.actions.ProvideParentCourseAction"/>

    <map:action name="setup-problem-for-staff"
                src="net.mumie.cocoon.actions.SetupProblemForStaffAction"/>

    <map:action name="setup-worksheet-for-staff"
                src="net.mumie.cocoon.actions.SetupWorksheetForStaffAction"/>

    <map:action name="setup-course-for-staff"
                src="net.mumie.cocoon.actions.SetupCourseForStaffAction"/>

    <map:action name="sync"
                src="net.mumie.cocoon.actions.SyncAction"/>

    <map:action name="qf-applet"
                src="net.mumie.cocoon.actions.QFAppletAction">
      <to-name>@qf-applet-mail-to-name@</to-name>
      <to-address>@qf-applet-mail-to-address@</to-address>
    </map:action>

    <map:action name="string-equal"
                src="net.mumie.cocoon.actions.StringEqualAction"/>

    <map:action name="prepare-selftest"
                src="net.mumie.cocoon.actions.PrepareSelftestAction"/>

    <map:action name="provide-section"
                src="net.mumie.cocoon.actions.ProvideSectionAction"/>

    <map:action name="provide-selftest-infos"
                src="net.mumie.cocoon.actions.ProvideSelftestInfosAction"/>

    <map:action name="setup-selftest-problem-for-staff"
                src="net.mumie.cocoon.actions.SetupSelftestProblemForStaffAction"/>

    <map:action name="change-password"
                src="net.mumie.cocoon.actions.ChangePasswordAction"/>

    <map:action name="move-content-object"
                src="net.mumie.cocoon.actions.MoveContentObjectAction"/>

    <map:action name="check-if-staff"
                src="net.mumie.cocoon.actions.CheckIfStaffAction"/>

    <map:action name="change-language"
                src="net.mumie.cocoon.actions.ChangeLanguageAction"/>

    <map:action name="check-if-non-empty"
                src="net.mumie.cocoon.actions.CheckIfNonEmptyAction"/>

    <map:action name="change-theme"
                src="net.mumie.cocoon.actions.ChangeThemeAction"/>

    <map:action name="worksheet-tutorial-bulk-correction"
                src="net.mumie.cocoon.actions.WorksheetTutorialBulkCorrectionAction"/>

    <map:action name="create-account"
                src="net.mumie.cocoon.actions.CreateAccountAction"/>

    <map:action name="edit-user"
                src="net.mumie.cocoon.actions.EditUserAction"/>

    <map:action name="change-tutorial"
                src="net.mumie.cocoon.actions.ChangeTutorialAction"/>

    <map:action name="edit-tutorial-unpriv"
                src="net.mumie.cocoon.actions.EditTutorialUnprivAction"/>

    <map:action name="setup-training-problem"
                src="net.mumie.cocoon.actions.SetupTrainingProblemAction"/>

    <map:action name="check-receipt"
                src="net.mumie.cocoon.actions.CheckReceiptAction"/>

    <map:action name="provide-default-worksheet-ref"
                src="net.mumie.cocoon.actions.ProvideDefaultWorksheetRefAction"/>

    <map:action name="setup-session" src="net.mumie.cocoon.actions.SetupSessionAction"/>

  </map:actions>

<!-- ==================================================================================== -->
<!-- h1: Matchers                                                                         -->
<!-- ==================================================================================== -->
  
  <map:matchers default="wildcard">

    <map:matcher name="wildcard"
                 src="org.apache.cocoon.matching.WildcardURIMatcher"/>

    <map:matcher name="list"
                 src="net.mumie.cocoon.matchers.ListURIMatcher"/>

  </map:matchers>

<!-- ==================================================================================== -->
<!-- h1: Selectors                                                                        -->
<!-- ==================================================================================== -->
  
  <map:selectors default="browser">

    <map:selector name="browser"
                  src="org.apache.cocoon.selection.BrowserSelector">
      <browser name="opera" useragent="Opera"/>
      <browser name="explorer" useragent="MSIE"/>
      <browser name="konqueror" useragent="Konqueror"/>
      <browser name="mozilla" useragent="Gecko"/>
      <browser name="japs-client" useragent="JapsClient"/>
    </map:selector>

    <map:selector name="simple"
                  src="org.apache.cocoon.selection.SimpleSelector"/>
  </map:selectors>

<!-- ==================================================================================== -->
<!-- h1: Pipes                                                                            -->
<!-- ==================================================================================== -->

  <map:pipes default="caching">

    <map:pipe name="caching"
              src="org.apache.cocoon.components.pipeline.impl.CachingProcessingPipeline"/>

    <map:pipe name="non-caching"
              src="org.apache.cocoon.components.pipeline.impl.NonCachingProcessingPipeline"/>

  </map:pipes>

</map:components>

<!-- ==================================================================================== -->
<!-- h1: Views                                                                           -->
<!-- ==================================================================================== -->
   
<!--
<map:views>
  <map:view name="generator" from-label="generator">
    <map:serialize type="xml"/>
  </map:view>
  <map:view name="xml" from-label="xml">
    <map:serialize type="xml"/>
  </map:view>
</map:views>
-->

<!-- ==================================================================================== -->
<!-- h1: Resources                                                                       -->
<!-- ==================================================================================== -->

<map:resources>

  <map:resource name="xhtml">
    <map:select>
      <!-- Browser switch postponed
      <map:when test="opera">
      </map:when>
      <map:when test="explorer">
      </map:when>
      <map:when test="konqueror">
      </map:when>
      -->
      <map:otherwise>
        <map:serialize type="xhtml"/> 
      </map:otherwise>
    </map:select>
  </map:resource>

</map:resources>

<map:pipelines>

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

<!-- ==================================================================================== -->
<!-- h1: Internal URLs                                                                    -->
<!-- ==================================================================================== -->

  <map:pipeline internal-only="true">
    <map:match pattern="internal/auth/auth">
      <map:generate type="authentication"/>
      <map:serialize type="xml"/>
    </map:match>
  </map:pipeline>

<!-- ==================================================================================== -->
<!-- h1: Public URLs                                                                      -->
<!-- ==================================================================================== -->
  
  <map:pipeline>

    <!-- Images, CSS stylesheets, ... -->
    <map:match pattern="public/resources/*">
      <map:read type="resource" src="fs_content/resources/{1}"/>
    </map:match>

    <!-- ============================================================================ -->
    <!-- h2: Login/logout                                                             -->
    <!-- ============================================================================ -->

    <!-- Asks the user for login with name and password -->
    <map:match pattern="public/auth/login-request">
      <map:act type="check-if-logged-in">
        <map:parameter name="handler" value="user"/>
        <map:redirect-to smac:uri="add-url-prefix(protected/auth/already-logged-in)"/>
      </map:act>
      <map:select>
        <map:when test="japs-client">
          <!-- Text version -->
          <map:act type="add-response-headers">
            <map:parameter name="X-Mumie-Login-Required" value="yes"/>
            <map:read type="string">
              <map:parameter name="string" value="Login required"/>
            </map:read>
          </map:act>
        </map:when>
        <map:otherwise>
          <!-- HTML version -->
          <map:act type="add-response-headers">
            <map:parameter name="X-Mumie-Login-Required" value="yes"/>
            <map:generate type="file" src="fs_content/login_form.xml"/>
            <map:transform src="fs_content/login_form.xsl">
              <map:parameter name="resource"
                             value="{request-param:resource}"/>
              <map:parameter name="default-resource"
                             smac:value="add-url-prefix(protected/auth/login-successful)"/>
              <map:parameter name="login-url"
                             smac:value="add-url-prefix(public/auth/login)"/>
            </map:transform>
            <map:serialize resource="xhtml"/>
          </map:act>
        </map:otherwise>
      </map:select>
    </map:match>

    <!-- Does the actual login -->
    <map:match pattern="public/auth/login">
      <map:act type="check-if-logged-in">
        <map:parameter value="user" name="handler"/>
        <map:redirect-to smac:uri="add-url-prefix(protected/auth/already-logged-in)"/>
      </map:act>
      <map:act type="login">
        <map:parameter value="user" name="handler"/>
        <map:parameter name="parameter_login-name" value="{request-param:login-name}"/>
        <map:parameter name="parameter_password" value="{request-param:password}"/>
        <map:parameter name="parameter_resource" value="{request-param:resource}"/>
        <map:redirect-to uri="{request-param:resource}"/>
      </map:act>
      <map:redirect-to smac:uri="add-url-prefix(public/auth/login-failed)"/>
    </map:match>
    
    <!-- Response if login succeded: see "Protected URLs" -->

    <!-- Response if login failed -->
    <map:match pattern="public/auth/login-failed">
      <map:select>
        <map:when test="japs-client">
          <!-- Text version -->
          <map:act type="add-response-headers">
            <map:parameter name="X-Mumie-Login-Required" value="yes"/>
            <map:read type="string">
              <map:parameter name="string" value="Login failed"/>
            </map:read>
          </map:act>
        </map:when>
        <map:otherwise>
          <!-- HTML version -->
          <map:generate type="file" src="fs_content/login_form.xml"/>
          <map:transform src="fs_content/login_form.xsl">
            <map:parameter name="error" value="true"/>
            <map:parameter name="resource"
                           value="{request-param:resource}"/>
            <map:parameter name="default-resource"
                           smac:value="add-url-prefix(protected/auth/login-successful)"/>
            <map:parameter name="login-url"
                           smac:value="add-url-prefix(public/auth/login)"/>
          </map:transform>
          <map:serialize resource="xhtml"/>
        </map:otherwise>
      </map:select>
    </map:match>

    <!-- Logout -->
    <map:match pattern="public/auth/logout">
      <map:act type="check-if-logged-in">
        <map:parameter name="handler" value="user"/>
        <map:act type="logout">
          <map:parameter name="handler" value="user"/>
          <map:select>
            <map:when test="japs-client">
              <!-- Text version -->
              <map:read type="string">
                <map:parameter name="string" value="Logout successful"/>
              </map:read>
            </map:when>
            <map:otherwise>
              <map:redirect-to smac:uri="add-url-prefix(public/auth/login-request)"/>
            </map:otherwise>
          </map:select>
        </map:act>
        <map:redirect-to smac:uri="add-url-prefix(public/auth/logout-failed)"/>
      </map:act>
      <map:redirect-to smac:uri="add-url-prefix(public/auth/not-logged-in)"/>
    </map:match>

    <!-- Response if logout failed -->
    <map:match pattern="public/auth/logout-failed">
      <map:select>
        <map:when test="japs-client">
          <!-- Text version -->
          <map:act type="add-response-headers">
            <map:parameter name="X-Mumie-Status" value="ERROR"/>
            <map:parameter name="X-Mumie-Error" value="Logout failed"/>
            <map:read type="string">
            <map:parameter name="string" value="Logout failed"/>
            </map:read>
          </map:act>
        </map:when>
        <map:otherwise>
          <!-- HTML version -->
          <map:generate type="file" src="fs_content/logout_failed.xhtml"/>
          <map:serialize resource="xhtml"/>
        </map:otherwise>
      </map:select>
    </map:match>

    <!-- Response if logout is unnecessary because the user is not logged in: -->
    <map:match pattern="public/auth/not-logged-in">
      <map:select>
        <map:when test="japs-client">
          <!-- Text version -->
          <map:read type="string">
            <map:parameter name="string" value="Not logged-in"/>
          </map:read>
        </map:when>
        <map:otherwise>
          <!-- HTML version -->
          <map:generate type="file" src="fs_content/not_logged_in.xhtml"/>
          <map:serialize resource="xhtml"/>
        </map:otherwise>
      </map:select>
    </map:match>

    <!-- Check if logged-in -->
    <map:match pattern="public/auth/status">
      <map:act type="check-if-logged-in">
        <map:parameter name="handler" value="user"/>
        <map:read type="string">
          <map:parameter name="string" value="Logged-in"/>
        </map:read>
      </map:act>
      <map:read type="string">
        <map:parameter name="string" value="Not logged-in"/>
      </map:read>
    </map:match>

    <!-- ============================================================================ -->
    <!-- h2: Account creation                                                         -->
    <!-- ============================================================================ -->

    <map:match pattern="public/auth/create-account-form">
      <map:generate type="file" src="fs_content/create_account_form.xml"/>
      <map:transform src="fs_content/transform.xsl">
        <map:parameter name="url-prefix" value="@url-prefix@"/>
      </map:transform>
      <map:serialize resource="xhtml"/>
    </map:match>

    <map:match pattern="public/auth/create-account">
      <map:act type="create-account">
        <map:parameter name="login-name" value="{request-param:login-name}"/>
        <map:parameter name="first-name" value="{request-param:first-name}"/>
        <map:parameter name="surname" value="{request-param:surname}"/>
        <map:parameter name="e-mail" value="{request-param:e-mail}"/>
        <map:parameter name="password" value="{request-param:password}"/>
        <map:parameter name="password-retyped" value="{request-param:password-retyped}"/>
        <map:parameter name="user-groups" smac:value="ugr-names-to-ids(students)"/>
        <!-- <map:parameter name="tutorials" smac:value="psdoc-paths-to-ids(tutorial, org/tub/universal/tutorials/all.meta.xml)"/> -->
        <map:select type="simple">
          <map:parameter name="value" value="{case}"/>
          <map:when test="ok">
            <!-- Login the new user: -->
            <map:act type="login">
              <map:parameter value="user" name="handler"/>
              <map:parameter name="parameter_login-name" value="{request-param:login-name}"/>
              <map:parameter name="parameter_password" value="{request-param:password}"/>
              <map:parameter name="parameter_resource" value="{request-param:resource}"/>
              <map:redirect-to uri="{request-param:resource}"/>
            </map:act>
            <map:redirect-to smac:uri="add-url-prefix(public/auth/login-failed)"/>
          </map:when>
          <map:otherwise>
            <map:generate type="file" src="fs_content/create_account_form.xml"/>
            <map:transform src="fs_content/transform.xsl">
              <map:parameter name="url-prefix" value="@url-prefix@"/>
              <map:parameter name="error" value="{error}"/>
            </map:transform>
            <map:serialize resource="xhtml"/>
          </map:otherwise>
        </map:select>
      </map:act>
    </map:match>

  </map:pipeline>


<!-- ==================================================================================== -->
<!-- h1: Protected URLs                                                                   -->
<!-- ==================================================================================== -->
  
  <map:pipeline>

    <map:match pattern="protected/**">
      <map:act type="protect">
        <map:parameter value="user" name="handler"/>

        <map:act type="setup-session">
          <map:parameter name="course" value="{request-param:course}"/>
          <map:parameter name="ref" value="{request-param:ref}"/>
          <map:parameter name="class" value="{request-param:class}"/>
          <map:parameter name="url" value="{../1}"/>
        </map:act>

        <!-- ============================================================================ -->
        <!-- h2: Context "auth"                                                           -->
        <!-- ============================================================================ -->

        <!-- Response if login was successful -->
        <map:match pattern="protected/auth/login-successful">
          <map:select>
            <map:when test="japs-client">
              <!-- Text version -->
              <map:read type="string">
                <map:parameter name="string" value="Login successful"/>
              </map:read>
            </map:when>
            <map:otherwise>
              <!-- HTML version -->
              <map:redirect-to smac:uri="add-url-prefix(protected/alias/start)"/>
            </map:otherwise>
          </map:select>
        </map:match>

        <!-- Displayed if user is already logged-in -->
        <map:match pattern="protected/auth/already-logged-in">
          <map:select>
            <map:when test="japs-client">
              <!-- Text version -->
              <map:read type="string">
                <map:parameter name="string" value="Session exists (already logged in)"/>
              </map:read>
            </map:when>
            <map:otherwise>
              <!-- HTML version -->
              <map:redirect-to
                 smac:uri="resolve-path(
                             %{prefix}/protected/view/document/type-name/generic_page/id/%{id},
                             system/misc/g_pge_already_logged_in.meta.xml,
                             generic_page)"/>
            </map:otherwise>
          </map:select>
        </map:match>

        <!-- Check if user is logged in -->
        <map:match pattern="protected/auth/check">
          <map:read type="string">
            <map:parameter name="string" value="Ok"/>
          </map:read>
        </map:match>

        <!-- Changes the users' password -->
        <map:match pattern="protected/auth/change-password">

          <map:act type="change-password">
            <map:parameter name="old-password" value="{request-param:old-password}"/>
            <map:parameter name="new-password" value="{request-param:new-password}"/>
            <map:parameter name="new-password-repeated" value="{request-param:new-password-repeated}"/>

            <map:act type="resolve-generic-document">
              <map:parameter name="name-of-generic" value="generic_page"/>
              <map:parameter name="id-of-generic"
                             smac:value="resolve-path(
                                           %{id},
                                           system/account/g_pge_change_password.meta.xml,
                                           generic_page)"/>
              <map:parameter name="language" value="{session-attr:language}"/>
              <map:parameter name="theme" value="{session-attr:theme}"/>
            
              <map:generate type="document">
                <map:parameter name="type-name" value="page"/>
                <map:parameter name="id" value="{id-of-real}"/>
              </map:generate>

              <map:transform type="add-params">
                <map:parameter name="status" value="{../status}"/>
              </map:transform>
              <map:transform type="add-user"/>
              <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/misc/g_xsl_page.meta.xml,generic_xsl_stylesheet)" label="xml"/>

              <map:call resource="xhtml"/>

            </map:act>

          </map:act>

        </map:match>        

        <!-- ============================================================================ -->
        <!-- h2: Context "error"                                                          -->
        <!-- ============================================================================ -->

        <!-- Permission denied -->
        <map:match pattern="protected/error/permission-denied">
          <map:select>
            <map:when test="japs-client">
              <!-- Text version -->
              <map:act type="add-response-headers">
                <map:parameter name="X-Mumie-Error" value="Permission denied"/>
                <map:read type="string" status-code="403">
                  <map:parameter name="string" value="Permission denied"/>
                </map:read>
              </map:act>
            </map:when>
            <map:otherwise>
              <!-- HTML version -->
              <map:redirect-to
                smac:uri="resolve-path
                            (%{prefix}/protected/view/document/type-name/generic_page/id/%{id},
                             system/misc/g_pge_permission_denied.meta.xml,
                             generic_page)"/>
            </map:otherwise>
          </map:select>
        </map:match>

        <!-- ============================================================================ -->
        <!-- h2: Context "info"                                                           -->
        <!-- ============================================================================ -->

        <map:match pattern="protected/info/**">

          <!-- User group check -->
          <map:act type="check-user-groups">
            <map:parameter name="user-groups"
                           smac:value="ugr-names-to-ids(tutors,lecturers,admins)"/>

            <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
            <!-- h3: Documents                                                            -->
            <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->

            <!-- Applets, Java 6 bug workaround: -->
            <map:match pattern="protected/info/document/type-name/applet/id/*.jar">

              <!-- Read permission check -->
              <map:act type="check-read-permission">
                <map:parameter name="type" smac:value="doctype-name-to-code(applet)"/>
                <map:parameter name="id" value="{1}"/>

                <map:generate type="document">
                  <map:parameter name="type" smac:value="doctype-name-to-code(applet)"/>
                  <map:parameter name="id" value="{../1}"/>
                  <map:parameter name="use-mode-name" value="info"/>
                  <map:parameter name="with-path" value="true"/>
                </map:generate>
                <map:transform type="add-user" label="xml"/>
                <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme}, system/document/g_xsl_document_info.meta.xml, generic_xsl_stylesheet)"/>
                <map:serialize resource="xhtml"/>

              </map:act> <!-- End of check-read-permission action -->

              <!-- Redirect to error page -->
              <map:redirect-to smac:uri="add-url-prefix(protected/error/permission-denied)"/>

            </map:match> <!-- End of "protected/info/document/type-name/applet/id/*.jar" matcher -->

            <!-- Jars, Java 6 bug workaround: -->
            <map:match pattern="protected/info/document/type-name/jar/id/*.jar">

              <!-- Read permission check -->
              <map:act type="check-read-permission">
                <map:parameter name="type" smac:value="doctype-name-to-code(jar)"/>
                <map:parameter name="id" value="{1}"/>

                <map:generate type="document">
                  <map:parameter name="type" smac:value="doctype-name-to-code(jar)"/>
                  <map:parameter name="id" value="{../1}"/>
                  <map:parameter name="use-mode-name" value="info"/>
                  <map:parameter name="with-path" value="true"/>
                </map:generate>
                <map:transform type="add-user" label="xml"/>
                <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme}, system/document/g_xsl_document_info.meta.xml, generic_xsl_stylesheet)"/>
                <map:serialize resource="xhtml"/>

              </map:act> <!-- End of check-read-permission action -->

              <!-- Redirect to error page -->
              <map:redirect-to smac:uri="add-url-prefix(protected/error/permission-denied)"/>

            </map:match> <!-- End of "protected/info/document/type-name/applet/id/*.jar" matcher -->

            <!-- Workaround for generic documents: Bypass read permission check (there is no
              read permission table for generic documents yet :-(  -->
            <map:match pattern="protected/info/document/type-name/generic_*/id/*">

              <map:generate type="document">
                <map:parameter name="type-name" value="generic_{1}"/>
                <map:parameter name="id" value="{2}"/>
                <map:parameter name="use-mode-name" value="info"/>
                <map:parameter name="with-path" value="true"/>
              </map:generate>
              <map:transform type="add-user" label="xml"/>                        
              <map:transform type="add-params">
                <map:parameter name="as-popup" value="{request-param:as-popup}"/>
               </map:transform>
              <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme}, system/document/g_xsl_document_info.meta.xml, generic_xsl_stylesheet)"/>
              <map:serialize resource="xhtml"/>
              
            </map:match> <!-- End of "protected/info/document/type-name/generic_*/id/*" matcher -->

            <!-- Other types -->
            <map:match pattern="protected/info/document/type-name/*/id/*">

              <!-- Read permission check -->
              <map:act type="check-read-permission">
                <map:parameter name="type-name" value="{1}"/>
                <map:parameter name="id" value="{2}"/>

                <map:generate type="document">
                  <map:parameter name="type-name" value="{../1}"/>
                  <map:parameter name="id" value="{../2}"/>
                  <map:parameter name="use-mode-name" value="info"/>
                  <map:parameter name="with-path" value="true"/>
                </map:generate>
                <map:transform type="add-user" label="xml"/>
                <map:transform type="add-params">
                  <map:parameter name="as-popup" value="{request-param:as-popup}"/>
                </map:transform>
                <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme}, system/document/g_xsl_document_info.meta.xml, generic_xsl_stylesheet)"/>
                <map:serialize resource="xhtml"/>

              </map:act> <!-- End of check-read-permission action -->

              <!-- Redirect to error page -->
              <map:redirect-to smac:uri="add-url-prefix(protected/error/permission-denied)"/>

            </map:match> <!-- End of "protected/info/document/type-name/*/id/*" matcher -->

            <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
            <!-- h3: Pseudo-documents                                                     -->
            <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->

            <map:match pattern="protected/info/pseudo-document/type-name/*/id/*">

              <!-- User group check -->
              <map:act type="check-user-groups">
                <map:parameter name="user-groups"
                               smac:value="ugr-names-to-ids(tutors,lecturers,admins)"/>

                <!-- Read permission check -->
                <map:act type="check-pseudodoc-read-permission">
                  <map:parameter name="type-name" value="{../1}"/>
                  <map:parameter name="id" value="{../2}"/>

                  <!-- Sections: -->
                  <map:match pattern="protected/info/pseudo-document/type-name/section/id/*">
                    <map:generate type="pseudo-document">
                      <map:parameter name="type-name" value="section"/>
                      <map:parameter name="id" value="{1}"/>
                      <map:parameter name="with-path" value="true"/>
                    </map:generate>
                    <map:transform type="add-user" label="xml"/>
                    <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/misc/g_xsl_section.meta.xml,generic_xsl_stylesheet)"/>
                    <map:call resource="xhtml"/>
                  </map:match>

                  <!-- Other: -->
                  <map:match pattern="protected/info/pseudo-document/type-name/*/id/*">
                    <map:generate type="pseudo-document">
                      <map:parameter name="type-name" value="{1}"/>
                      <map:parameter name="id" value="{2}"/>
                      <map:parameter name="with-path" value="true"/>
                    </map:generate>
                    <map:transform type="add-user" label="xml"/>
                    <map:transform smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme}, system/pseudodoc/g_xsl_pseudodoc.meta.xml, generic_xsl_stylesheet)"/>
                    <map:call resource="xhtml"/>
                  </map:match>
              
                </map:act> <!-- End of check-read-permission action -->

                <!-- Redirect to error page -->
                <map:redirect-to smac:uri="add-url-prefix(protected/error/permission-denied)"/>
              
              </map:act> <!-- End of check-user-group action -->

              <!-- Redirect to error page -->
              <map:redirect-to smac:uri="add-url-prefix(protected/error/permission-denied)"/>

            </map:match> <!-- End of "protected/info/pseudo-document/type-name/*/id/*"  matcher -->

          </map:act> <!-- End of check-user-groups action -->

          <!-- Redirect to error page -->
          <map:redirect-to smac:uri="add-url-prefix(protected/error/permission-denied)"/>

        </map:match> <!-- End of protected/info/** matcher -->

        <!-- ============================================================================ -->
        <!-- h2: Resolving generic documents                                              -->
        <!-- ============================================================================ -->

        <map:match pattern="protected/*/document/type-name/generic_*/id/*">
          <map:parameter name="class" value="{request-param:class}"/>
          <map:act type="resolve-generic-document">
            <map:parameter name="name-of-generic" value="generic_{2}"/>
            <map:parameter name="id-of-generic" value="{3}"/>
            <map:parameter name="language" value="{session-attr:language}"/>
            <map:parameter name="theme" value="{session-attr:theme}"/>
            <map:redirect-to uri="cocoon:/protected/{../1}/document/type-name/{name-of-real}/id/{id-of-real}"/>
            </map:act>
        </map:match>

        <map:match pattern="protected/*/document/type-name/generic_*/id/*/cache-hint/*/*">
          <map:parameter name="class" value="{request-param:class}"/>
          <map:act type="resolve-generic-document">
            <map:parameter name="name-of-generic" value="generic_{2}"/>
            <map:parameter name="id-of-generic" value="{3}"/>
            <map:parameter name="language" value="{session-attr:language}"/>
            <map:parameter name="theme" value="{session-attr:theme}"/>
            <map:redirect-to uri="cocoon:/protected/{../1}/document/type-name/{name-of-real}/id/{id-of-real}"/>
            </map:act>
        </map:match>

        <!-- ============================================================================ -->
        <!-- h2: Resolving documents specified by vc threads                              -->
        <!-- ============================================================================ -->

        <map:match pattern="protected/*/document/type-name/*/vc-thread/*">
          <map:act type="resolve-vc-thread">
            <map:parameter name="type-name" value="{2}"/>
            <map:parameter name="vc-thread" value="{3}"/>
            <map:redirect-to uri="cocoon:/protected/{../1}/document/type-name/{../2}/id/{id}"/>
          </map:act>
        </map:match>

        <!-- ============================================================================ -->
        <!-- h2: Context "view", documents                                              -->
        <!-- ============================================================================ -->

        <map:match pattern="protected/view/document/type-name/*/id/*.jar">

          <!-- Read permission check -->
          <map:act type="check-read-permission">
            <map:parameter name="type-name" value="{1}"/>
            <map:parameter name="id" value="{2}"/>

            <!-- Applets, Java 6 bug workaround: -->
            <map:match pattern="protected/view/document/type-name/applet/id/*.jar">
              <map:read>
                <map:parameter name="type" smac:value="doctype-name-to-code(applet)"/>
                <map:parameter name="id" value="{1}"/>
              </map:read>
            </map:match>
            
            <!-- Jars, Java 6 bug workaround -->
            <map:match pattern="protected/view/document/type-name/jar/id/*.jar">
              <map:read>
                <map:parameter name="type" smac:value="doctype-name-to-code(jar)"/>
                <map:parameter name="id" value="{1}"/>
              </map:read>
            </map:match>

          </map:act> <!-- End of check-read-permission action -->

          <!-- Redirect to error page -->
          <map:redirect-to smac:uri="add-url-prefix(protected/error/permission-denied)"/>

        </map:match> <!-- End of "protected/view/document/type-name/*/id/*.jar"  matcher -->

        <map:match pattern="protected/view/document/type-name/*/id/*">

          <!-- Read permission check -->
          <map:act type="check-read-permission">
            <map:parameter name="type-name" value="{1}"/>
            <map:parameter name="id" value="{2}"/>

            <!-- Pages -->
            <map:match pattern="protected/view/document/type-name/page/id/*">
              <map:generate>
                <map:parameter name="type" smac:value="doctype-name-to-code(page)"/>
                <map:parameter name="id" value="{1}"/>
              </map:generate>
              <map:transform type="add-request-params"/>
              <map:transform type="add-user"/>
              <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/misc/g_xsl_page.meta.xml,generic_xsl_stylesheet)"/>
              <map:call resource="xhtml"/>
            </map:match>

            <!-- XSL stylesheets -->
            <map:match pattern="protected/view/document/type-name/xsl_stylesheet/id/*">
              <map:generate>
                <map:parameter name="type" smac:value="doctype-name-to-code(xsl_stylesheet)"/>
                <map:parameter name="id" value="{1}"/>
              </map:generate>
              <map:transform type="add-params">
                <map:parameter name="lang-id" value="{session-attr:language}"/>
                <map:parameter name="theme-id" value="{session-attr:theme}"/>
              </map:transform>
              <map:transform src="resource://net/mumie/cocoon/transformers/rootxsl.xsl"/>
              <map:serialize type="xml"/>
            </map:match>

            <!-- CSS Stylesheets -->
            <map:match pattern="protected/view/document/type-name/css_stylesheet/id/*">
              <map:generate>
                <map:parameter name="type" smac:value="doctype-name-to-code(css_stylesheet)"/>
                <map:parameter name="id" value="{1}"/>
              </map:generate>
              <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/misc/g_xsl_css.meta.xml,generic_xsl_stylesheet)"/>
              <map:serialize type="css"/>
            </map:match>

            <!-- JS libs -->
            <map:match pattern="protected/view/document/type-name/js_lib/id/*">
              <map:generate>
                <map:parameter name="type" smac:value="doctype-name-to-code(js_lib)"/>
                <map:parameter name="id" value="{1}"/>
              </map:generate>
              <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/misc/g_xsl_text.meta.xml,generic_xsl_stylesheet)"/>
              <map:serialize type="javascript"/>
            </map:match>

            <!-- Elements -->
            <map:match pattern="protected/view/document/type-name/element/id/*">
              <map:generate>
                <map:parameter name="type" smac:value="doctype-name-to-code(element)"/>
                <map:parameter name="id" value="{1}"/>
              </map:generate>
              <map:transform type="add-params">
                <map:parameter name="as-frame" value="{request-param:as-frame}"/>
                <map:parameter name="as-popup" value="{request-param:as-popup}"/>
              </map:transform>
              <map:transform type="add-user">
                <map:parameter name="id" value="{user}"/>
              </map:transform>
              <map:transform type="xslt"
                smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/element/g_xsl_element.meta.xml,generic_xsl_stylesheet)"/>
              <map:call resource="xhtml"/>
            </map:match>

            <!-- Subelements -->
            <map:match pattern="protected/view/document/type-name/subelement/id/*">
              <map:generate>
                <map:parameter name="type" smac:value="doctype-name-to-code(subelement)"/>
                <map:parameter name="id" value="{1}"/>
              </map:generate>
              <map:transform type="add-params">
                <map:parameter name="as-frame" value="{request-param:as-frame}"/>
                <map:parameter name="as-popup" value="{request-param:as-popup}"/>
              </map:transform>
              <map:transform type="add-user">
                <map:parameter name="id" value="{user}"/>
              </map:transform>
              <map:transform type="xslt"
                 smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/element/g_xsl_element.meta.xml,generic_xsl_stylesheet)"/>
              <map:call resource="xhtml"/>
            </map:match>

            <!-- Problems -->
            <map:match pattern="protected/view/document/type-name/problem/id/*">
              <map:act type="provide-default-worksheet-ref">
                <map:parameter name="problem-id" value="{1}"/>
                <map:redirect-to uri="cocoon:/protected/homework/document/type-name/problem/id/{../1}?ref={ref}"/>
              </map:act>
            </map:match>

            <!-- Courses -->
            <map:match pattern="protected/view/document/type-name/course/id/*">
              <map:generate>
                <map:parameter name="type" smac:value="doctype-name-to-code(course)"/>
                <map:parameter name="id" value="{1}"/>
              </map:generate>
              <map:transform type="add-request-params"/>
              <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/course/g_xsl_course.meta.xml,generic_xsl_stylesheet)"/>

              <map:call resource="xhtml"/>
            </map:match>

            <!-- Course Sections -->
            <map:match pattern="protected/view/document/type-name/course_section/id/*">
              <map:act type="provide-course-section-infos">
                <map:parameter name="course-section" value="{1}"/>
                <!-- This action provides the following sitemap parameters used below
                (it sets some more, but only these are used):
                course
                  Id of the latest parent course 
                label
                  Label of the course_section in that course
                summary
                  Id of the generic summary
                -->

              <map:act type="resolve-generic-document">
                <map:parameter name="name-of-generic" value="generic_summary"/>
                <map:parameter name="id-of-generic" value="{summary}"/>
                <map:parameter name="language" value="{session-attr:language}"/>
                <map:parameter name="theme" value="{session-attr:theme}"/>
                
                <map:generate type="document">
                  <map:parameter name="type-name" value="summary"/>
                  <map:parameter name="id" value="{id-of-real}"/>
                </map:generate>
                
                <map:transform type="add-params">
                  <map:parameter name="course-section" value="{../1}"/>
                  <map:parameter name="course-section-label" value="{label}"/>
                  <map:parameter name="session-user" value="{session-attr:user}"/>
                  <map:parameter name="with-load-button" value="{request-param:with-load-button}"/>
                  <map:parameter name="as-frame" value="{request-param:as-frame}"/>
                </map:transform>

                <map:transform type="add-user">
                  <map:parameter name="id" value="{user}"/>
                </map:transform>

                <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme}, system/course/g_xsl_course_section_summary.meta.xml, generic_xsl_stylesheet)"/>
                <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/xsl_stylesheet/id/%{id}, system/common/xsl_math_signfix.meta.xml, xsl_stylesheet)"/>
                
                <map:call resource="xhtml"/>
                
              </map:act> <!-- End of resolve-generic-document action -->
              </map:act> <!-- End of provide-course-section-infos action -->
            </map:match>
            
            <!-- Worksheets -->
            <map:match pattern="protected/view/document/type-name/worksheet/id/*">
              <map:generate>
                <map:parameter name="type" smac:value="doctype-name-to-code(worksheet)"/>
                <map:parameter name="id" value="{1}"/>
              </map:generate>
              <map:transform type="add-request-params"/>
              <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/course/g_xsl_course.meta.xml,generic_xsl_stylesheet)"/>
              <map:call resource="xhtml"/>
            </map:match>

            <!-- Images -->
            <map:match pattern="protected/view/document/type-name/image/id/*">
              <map:read>
                <map:parameter name="type" smac:value="doctype-name-to-code(image)"/>
                <map:parameter name="id" value="{1}"/>
              </map:read>
            </map:match>
            
            <!-- Flashs -->
            <map:match pattern="protected/view/document/type-name/flash/id/*">
              <map:read>
                <map:parameter name="type" smac:value="doctype-name-to-code(flash)"/>
                <map:parameter name="id" value="{1}"/>
              </map:read>
            </map:match>

            <!-- summarys -->
            <map:match pattern="protected/view/document/type-name/summary/id/*">
              <map:generate>
                <map:parameter name="type" smac:value="doctype-name-to-code(summary)"/>
                <map:parameter name="id" value="{1}"/>
              </map:generate>
              <map:transform src="resource://net/mumie/cocoon/transformers/rootxsl.xsl"/>
              <map:serialize type="xml"/>
            </map:match>

          </map:act> <!-- End of check-read-permission action -->

          <!-- Redirect to error page -->
          <map:redirect-to smac:uri="add-url-prefix(protected/error/permission-denied)"/>

        </map:match> <!-- End of "protected/view/document/type-name/*/id/*"  matcher -->


        <!-- ============================================================================ -->
        <!-- h2: Context "view", grades                                                   -->
        <!-- ============================================================================ -->

        <map:match pattern="protected/view/tutorial-user-problem-grades">

          <!-- Check if the user is a staff member: -->
          <map:act type="check-if-staff">
            <map:parameter name="course" value="{request-param:course}"/>

            <!-- If necessary, correct one worksheet: -->
            <map:act type="check-if-non-empty">
              <map:parameter name="test" value="{request-param:correct-worksheet}"/>
              <map:act type="worksheet-tutorial-bulk-correction">
                <map:parameter name="tutorial" value="{request-param:tutorial}"/>
                <map:parameter name="worksheet" value="{request-param:correct-worksheet}"/>

                <map:generate type="tutorial-user-problem-grades" label="xml">
                  <map:parameter name="tutorial" value="{request-param:tutorial}"/>
                  <map:parameter name="course" value="{request-param:course}"/>
                </map:generate>

                <map:transform type="add-params">
                  <map:parameter name="worksheet" value="{request-param:correct-worksheet}"/>
                  <map:parameter name="num-corrections" value="{numCorrections}"/>
                  <map:parameter name="num-successful-corrections" value="{numSuccessfulCorrections}"/>
                </map:transform>

                <map:transform type="add-user"/>

                <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/grades/g_xsl_problem_grades_grouping.meta.xml,generic_xsl_stylesheet)"/>
                <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/grades/g_xsl_tutorial_user_problem_grades.meta.xml,generic_xsl_stylesheet)"/>
                <map:call resource="xhtml"/>
                
              </map:act>
            </map:act>

            <map:generate type="tutorial-user-problem-grades" label="xml">
              <map:parameter name="tutorial" value="{request-param:tutorial}"/>
              <map:parameter name="course" value="{request-param:course}"/>
            </map:generate>

            <map:transform type="add-user"/>

            <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/grades/g_xsl_problem_grades_grouping.meta.xml,generic_xsl_stylesheet)"/>
            <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/grades/g_xsl_tutorial_user_problem_grades.meta.xml,generic_xsl_stylesheet)"/>
            <map:call resource="xhtml"/>
            
          </map:act>

          <!-- Redirect to error page -->
          <map:redirect-to smac:uri="add-url-prefix(protected/error/permission-denied)"/>

        </map:match>

        
        <map:match pattern="protected/view/tutorial-user-problem-grades-csv">
          
          <!-- Check if the user is a staff member: -->
          <map:act type="check-if-staff">
            <map:parameter name="course" value="{request-param:course}"/>
    
            <map:generate type="tutorial-user-problem-grades" label="xml">
              <map:parameter name="tutorial" value="{request-param:tutorial}"/>
              <map:parameter name="course" value="{request-param:course}"/>
            </map:generate>
            
            <map:transform type="add-user"/>
            
            <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id},system/grades/g_xsl_problem_grades_grouping.meta.xml,generic_xsl_stylesheet)"/>
            <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id},system/grades/g_xsl_tutorial_user_problem_grades_csv_export.meta.xml,generic_xsl_stylesheet)"/>
            <map:serialize type="csv"/>
            
          </map:act>
          
          <!-- Redirect to error page -->
          <map:redirect-to smac:uri="add-url-prefix(protected/error/permission-denied)"/>
          
        </map:match>
        
        <map:match pattern="protected/view/class-user-problem-grades-csv">
          
          <!-- Check if the user is a staff member: -->
          <map:act type="check-if-staff">
            <map:parameter name="class" value="{request-param:class}"/>
            
            <map:generate type="class-user-problem-grades" label="xml">
              <map:parameter name="class" value="{request-param:class}"/>
            </map:generate>
            
            
            <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id},system/grades/g_xsl_problem_grades_grouping.meta.xml,generic_xsl_stylesheet)"/>
            <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id},system/grades/g_xsl_class_user_problem_grades_csv_export.meta.xml,generic_xsl_stylesheet)"/>
            <map:serialize type="csv"/>
            
          </map:act>
          
          <!-- Redirect to error page -->
          <map:redirect-to uri="http://clifford.math.tu-berlin.de:8003/cocoon/protected/error/permission-denied"/>
          
        </map:match>
        
        
        
        <map:match pattern="protected/view/traditional-problem-points">


          <!-- Check if the user is a staff member: -->
          <map:act type="check-if-staff">
            <map:parameter name="course" value="{request-param:course}"/>

            <map:generate type="tutorial-user-traditional-problem-grades" label="xml">
              <map:parameter name="tutorial" value="{request-param:tutorial}"/>
              <map:parameter name="course" value="{request-param:course}"/>
              <map:parameter name="store" value="{request-param:store}"/>
            </map:generate>

            <map:transform type="add-user"/>

            <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/grades/g_xsl_traditional_problem_points.meta.xml,generic_xsl_stylesheet)"/>
            <map:call resource="xhtml"/>
            
          </map:act>

            <!-- Redirect to error page -->
          <map:redirect-to smac:uri="add-url-prefix(protected/error/permission-denied)"/>

        </map:match>

        <map:match pattern="protected/view/user-problem-grades">


          <!-- Check if the user is a staff member: -->
          <map:act type="check-if-staff">
            <map:parameter name="course" value="{request-param:course}"/>

            <!-- Setup user and tutor_view for staff member -->
            <map:act type="setup-course-for-staff">

              <map:generate type="user-problem-grades" label="xml">
                <map:parameter name="user" value="{user}"/>
                <map:parameter name="course" value="{request-param:course}"/>
              </map:generate>

              <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/grades/g_xsl_problem_grades_grouping.meta.xml,generic_xsl_stylesheet)"/>
              <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/grades/g_xsl_user_problem_grades.meta.xml,generic_xsl_stylesheet)"/>
              <map:call resource="xhtml"/>

            </map:act>
          </map:act>


          <!-- If student -->
          <map:generate type="user-problem-grades" label="xml">
            <map:parameter name="user" value="{session-attr:user}"/>
            <map:parameter name="course" value="{request-param:course}"/>
          </map:generate>

          <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/grades/g_xsl_problem_grades_grouping.meta.xml,generic_xsl_stylesheet)"/>
          <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/grades/g_xsl_user_problem_grades.meta.xml,generic_xsl_stylesheet)"/>
          <map:call resource="xhtml"/>

            
        </map:match>

        <map:match pattern="protected/view/worksheet-user-problem-grades">



          <!-- Check if the user is a staff member: -->
          <map:act type="check-if-staff">
            <map:parameter name="course" value="{request-param:course}"/>

            <!-- Setup user and tutor_view for staff member -->
            <map:act type="setup-course-for-staff">

              <map:generate type="worksheet-user-problem-grades" label="xml">
                <map:parameter name="user" value="{user}"/>
                <map:parameter name="worksheet" value="{request-param:worksheet}"/>
              </map:generate>

              <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/grades/g_xsl_problem_grades_grouping.meta.xml,generic_xsl_stylesheet)"/>
              <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/grades/g_xsl_worksheet_user_problem_grades.meta.xml,generic_xsl_stylesheet)"/>
              <map:call resource="xhtml"/>
            </map:act>
          </map:act>

          <!-- If student -->
          <map:generate type="worksheet-user-problem-grades" label="xml">
            <map:parameter name="user" value="{session-attr:user}"/>
            <map:parameter name="worksheet" value="{request-param:worksheet}"/>
          </map:generate>

          <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/grades/g_xsl_problem_grades_grouping.meta.xml,generic_xsl_stylesheet)"/>
          <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/grades/g_xsl_worksheet_user_problem_grades.meta.xml,generic_xsl_stylesheet)"/>
          <map:call resource="xhtml"/>

        </map:match>

        <!-- ============================================================================ -->
        <!-- h2: Context "view", misc.                                                    -->
        <!-- ============================================================================ -->

        <map:match pattern="protected/view/tutorials-for-class">

          <map:parameter name="class" value="{request-param:class}"/>

          <map:generate type="courses-and-tutorials-index" label="xml">
            <map:parameter name="class" value="{request-param:class}"/>
          </map:generate>

          <map:transform type="add-user"/>
          <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/misc/g_xsl_tutorials_for_class.meta.xml,generic_xsl_stylesheet)"/>
          <map:call resource="xhtml"/>
            
        </map:match>

        <map:match pattern="protected/view/class-members">

          <!-- Check if the user is a staff member: -->
          <map:act type="check-if-staff">
            <map:parameter name="class" value="{request-param:class}"/>

            <map:generate type="class-members-index" label="xml">
              <map:parameter name="class" value="{request-param:class}"/>
            </map:generate>

            <map:transform type="add-user"/>

            <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/misc/g_xsl_class_members.meta.xml,generic_xsl_stylesheet)"/>
            <map:call resource="xhtml"/>
            
          </map:act>

          <!-- Redirect to error page -->
          <map:redirect-to smac:uri="add-url-prefix(protected/error/permission-denied)"/>

        </map:match>

        <map:match pattern="protected/view/tutorial-members">

          <!-- Check if the user is a staff member: -->
          <map:act type="check-if-staff">
            <map:parameter name="class" value="{request-param:class}"/>

            <map:generate type="pseudo-document">
              <map:parameter name="type-name" value="tutorial"/>
              <map:parameter name="id" value="{request-param:tutorial}"/>
            </map:generate>

            <map:transform type="add-user"/>
            <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/misc/g_xsl_tutorial_members.meta.xml,generic_xsl_stylesheet)"/>
            <map:call resource="xhtml"/>
            
          </map:act>

          <!-- Redirect to error page -->
          <map:redirect-to smac:uri="add-url-prefix(protected/error/permission-denied)"/>

        </map:match>

        <!-- ============================================================================ -->
        <!-- h2: Context "view", pseudo-document lists                                    -->
        <!-- ============================================================================ -->

        <!-- Pseudo-documents lists-->
        <map:match pattern="protected/view/pseudo-document-index/type-name/*">

          <!-- User group check -->
          <map:act type="check-user-groups">
            <map:parameter name="user-groups"
                           smac:value="ugr-names-to-ids(tutors,lecturers,admins)"/>

            <map:generate type="pseudo-document-index">
              <map:parameter name="type-name" value="{../1}"/>
              <map:parameter name="use-mode-name" value="{request-param:use-mode}"/>
            </map:generate>
            <map:transform type="add-user"/>
            <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme}, system/admin/g_xsl_pseudo_document_index.meta.xml, generic_xsl_stylesheet)"/>
            <map:call resource="xhtml"/>

          </map:act> <!-- End of check-user-groups action -->

          <!-- Redirect to error page -->
          <map:redirect-to smac:uri="add-url-prefix(protected/error/permission-denied)"/>

        </map:match> <!-- End of "protected/view/pseudo-document-index/**" matcher -->

        <!-- List of classes and courses -->
        <map:match pattern="protected/view/classes-and-courses-index">
          <map:generate type="classes-and-courses-index"/>
          <map:transform type="add-user" label="xml"/>
          <map:transform smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme}, system/misc/g_xsl_classes_and_courses.meta.xml, generic_xsl_stylesheet)"/>
          <map:call resource="xhtml"/>
        </map:match>

        <!-- ============================================================================ -->
        <!-- h2: Context "view", document lists                                           -->
        <!-- ============================================================================ -->

        <!-- Document list -->
        <map:match pattern="protected/view/document-index/type-name/*">

          <!-- User group check -->
          <map:act type="check-user-groups">
             <map:parameter name="user-groups" smac:value="ugr-names-to-ids(authors,lecturers,admins)"/>

            <map:generate type="document-index">
              <map:parameter name="type-name" value="{../1}"/>
              <map:parameter name="use-mode-name" value="{request-param:use-mode}"/>
              <map:parameter name="only-latest" value="false"/>
            </map:generate>
            <map:transform type="add-user"/>
            <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme}, system/admin/g_xsl_document_index.meta.xml, generic_xsl_stylesheet)"/>
            <map:call resource="xhtml"/>
          </map:act> <!-- End of check-user-groups action -->

          <!-- Redirect to error page -->
          <map:redirect-to smac:uri="add-url-prefix(protected/error/permission-denied)"/>

        </map:match> <!-- End of "protected/view/document-index/type-name/*" matcher -->

        <!-- ============================================================================ -->
        <!-- h2: Context "mmbrowser"                                                      -->
        <!-- ============================================================================ -->

        <!-- ============ -->
        <!-- h3: Sections -->
        <!-- ============ -->

        <map:match pattern="protected/mmbrowser/pseudo-document/type-name/section/id/*">

          <!-- User group check -->
          <map:act type="check-user-groups">
            <map:parameter name="user-groups"
                           smac:value="ugr-names-to-ids(tutors,lecturers,admins)"/>

            <!-- Read permission check -->
            <map:act type="check-pseudodoc-read-permission">
              <map:parameter name="type-name" value="section"/>
              <map:parameter name="id" value="{../1}"/>

              <map:generate type="pseudo-document">
                <map:parameter name="type-name" value="section"/>
                <map:parameter name="id" value="{../../1}"/>
                <map:parameter name="with-path" value="true"/>
              </map:generate>

              <map:transform type="add-params">
                <map:parameter name="part" value="{request-param:part}"/>
              </map:transform>

              <map:transform type="resolve-generic-section-entries">
                <map:parameter name="type-name" value="section"/>
                <map:parameter name="id" value="{../../1}"/>
                <map:parameter name="language" value="{session-attr:language}"/>
                <map:parameter name="theme" value="{session-attr:theme}"/>
              </map:transform>

              <map:transform smac:src="resolve-path(cocoon:/protected/view/document/type-name/xsl_stylesheet/id/%{id}, system/mmbrowser/xsl_mmbrowser_section_toc_filter.meta.xml, xsl_stylesheet)"/>

              <map:transform type="add-user" label="xml"/>

              <map:act type="provide-section">
                <map:parameter name="nature" value="pseudo-document"/>
                <map:parameter name="type-name" value="section"/>
                <map:parameter name="id" value="{../../1}"/>

                <map:transform type="add-pseudo-document">
                  <map:parameter name="type-name" value="section"/>
                  <map:parameter name="id" value="{section}"/>
                </map:transform>
                
              </map:act>

              <map:transform smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme}, system/mmbrowser/g_xsl_mmbrowser_section_toc.meta.xml, generic_xsl_stylesheet)"/>
                <map:call resource="xhtml"/>

              
            </map:act> <!-- End of check-read-permission action -->

            <!-- Redirect to error page -->
            <map:redirect-to smac:uri="add-url-prefix(protected/error/permission-denied)"/>
              
          </map:act> <!-- End of check-user-group action -->

          <!-- Redirect to error page -->
          <map:redirect-to smac:uri="add-url-prefix(protected/error/permission-denied)"/>
          
        </map:match>

        <!-- ============= -->
        <!-- h3: Documents -->
        <!-- ============= -->

        <map:match pattern="protected/mmbrowser/document/type-name/*/id/*">

          <!-- User group check -->
          <map:act type="check-user-groups">
            <map:parameter name="user-groups"
                           smac:value="ugr-names-to-ids(tutors,lecturers,admins)"/>

            <!-- Read permission check -->
            <map:act type="check-read-permission">
              <map:parameter name="type-name" value="{../1}"/>
              <map:parameter name="id" value="{../2}"/>

              <!-- Applets -->
              <map:match pattern="protected/mmbrowser/document/type-name/applet/id/*">

                <map:generate>
                  <map:parameter name="type" smac:value="doctype-name-to-code(applet)"/>
                  <map:parameter name="id" value="{1}"/>
                  <map:parameter name="use-mode" smac:value="use-mode-name-to-code(component)"/>
                </map:generate>

                <map:transform type="add-user" label="xml"/>

                <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme}, system/mmbrowser/g_xsl_applet_info.meta.xml, generic_xsl_stylesheet)"/>
                <map:call resource="xhtml"/>
                
              </map:match>

               <!-- Redirect to view -->
               <map:redirect-to smac:uri="add-url-prefix(protected/view/document/type-name/{../../1}/id/{../../2})"/>

            </map:act> <!-- End of check-read-permission action -->
            
            <!-- Redirect to error page -->
            <map:redirect-to smac:uri="add-url-prefix(protected/error/permission-denied)"/>
              
          </map:act> <!-- End of check-user-group action -->

          <!-- Redirect to error page -->
          <map:redirect-to smac:uri="add-url-prefix(protected/error/permission-denied)"/>
          
        </map:match>
        
        <!-- ============================================================================ -->
        <!-- h2: Context "index", pseudo-documents                                         -->
        <!-- ============================================================================ -->

        <map:match pattern="protected/index/pseudo-document/type-name/*/id/*">

          <!-- Read permission check -->
          <map:act type="check-pseudodoc-read-permission">
            <map:parameter name="type-name" value="{1}"/>
            <map:parameter name="id" value="{2}"/>

            <!-- Sections: -->
            <map:match pattern="protected/index/pseudo-document/type-name/section/id/*">
              <map:generate type="pseudo-document">
                <map:parameter name="type-name" value="section"/>
                <map:parameter name="id" value="{1}"/>
                <map:parameter name="with-path" value="true"/>
              </map:generate>
              <map:transform type="add-user" label="xml"/>
              <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/misc/g_xsl_section.meta.xml,generic_xsl_stylesheet)"/>
              <map:call resource="xhtml"/>
            </map:match>

          </map:act> <!-- End of check-read-permission action -->

          <!-- Redirect to error page -->
          <map:redirect-to smac:uri="add-url-prefix(protected/error/permission-denied)"/>

        </map:match> <!-- End of "protected/index/pseudo-document/type-name/*/id/*"  matcher -->

        <!-- ============================================================================ -->
        <!-- h2: Context "wrap"                                                           -->
        <!-- ============================================================================ -->

        <map:match pattern="protected/wrap/document/type-name/*/id/*">

          <!-- Read permission check -->
          <map:act type="check-read-permission">
            <map:parameter name="type-name" value="{1}"/>
            <map:parameter name="id" value="{2}"/>
            
            <!-- Applets -->
            <map:match pattern="protected/wrap/document/type-name/applet/id/*">
              <map:generate>
                <map:parameter name="type" smac:value="doctype-name-to-code(applet)"/>
                <map:parameter name="id" value="{1}"/>
                <map:parameter name="use-mode" smac:value="use-mode-name-to-code(component)"/>
              </map:generate>
              <map:transform type="add-user" label="xml"/>
              <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme}, system/document/g_xsl_wrap_applet.meta.xml, generic_xsl_stylesheet)"/>
              <map:call resource="xhtml"/>
            </map:match>

          </map:act> <!-- End of check-read-permission action -->

          <!-- Redirect to error page -->
          <map:redirect-to smac:uri="add-url-prefix(protected/error/permission-denied)"/>

        </map:match> <!-- End of "protected/view/document/type-name/*/id/*.jar"  matcher -->

        <!-- ============================================================================ -->
        <!-- h2: Context "data"                                                           -->
        <!-- ============================================================================ -->

        <map:match pattern="protected/data/pseudo-document/type-name/*/id/*">

          <!-- Read permission check -->
          <map:act type="check-pseudodoc-read-permission">
            <map:parameter name="type-name" value="{1}"/>
            <map:parameter name="id" value="{2}"/>
            
            <!-- Sections raw for CourseCreator: -->
            <map:match pattern="protected/data/pseudo-document/type-name/section/id/*">
              <map:generate type="pseudo-document">
                <map:parameter name="type-name" value="section"/>
                <map:parameter name="id" value="{1}"/>
                <map:parameter name="with-path" value="true"/>
              </map:generate>
              <map:serialize type="xml"/>
            </map:match>
            
          </map:act> <!-- End of check-read-permission action -->
          
          <!-- Redirect to error page -->
          <map:redirect-to smac:uri="add-url-prefix(protected/error/permission-denied)"/>

        </map:match> <!-- End of "protected/data/pseudo-document/type-name/*/id/*" matcher -->

        <map:match pattern="protected/data/document/type-name/*/id/*">

          <!-- Read permission check -->
          <map:act type="check-read-permission">
            <map:parameter name="type-name" value="{1}"/>
            <map:parameter name="id" value="{2}"/>

            <!-- User group check -->
            <map:act type="check-user-groups">
              <map:parameter name="user-groups" smac:value="ugr-names-to-ids(authors,lecturers,admins)"/>

              <map:generate type="document">
                <map:parameter name="type-name" value="{../../1}"/>
                <map:parameter name="id" value="{../../2}"/>
                <map:parameter name="use-mode-name" value="info"/>
              </map:generate>
              <map:transform type="add-request-params"/>
              <map:serialize resource="xml"/>

            </map:act> <!-- End of check-user-groups action -->

            <!-- Redirect to error page -->
            <map:redirect-to smac:uri="add-url-prefix(protected/error/permission-denied)"/>

          </map:act> <!-- End of check-read-permission action -->

          <!-- Redirect to error page -->
          <map:redirect-to smac:uri="add-url-prefix(protected/error/permission-denied)"/>

        </map:match> <!-- End of "protected/data/document/type-name/*/id/*" matcher -->

        <!-- Document list -->
        <map:match pattern="protected/data/document-index/type-name/*">

          <!-- User group check -->
          <map:act type="check-user-groups">
            <map:parameter name="user-groups" smac:value="ugr-names-to-ids(authors,lecturers,admins)"/>

            <map:generate type="document-index">
              <map:parameter name="type-name" value="{../1}"/>
              <map:parameter name="use-mode-name" value="{request-param:use-mode}"/>
              <map:parameter name="only-latest" value="{request-param:only-latest}"/>
            </map:generate>
            <map:serialize type="xml" />

          </map:act> <!-- End of check-user-groups action -->

          <!-- Redirect to error page -->
          <map:redirect-to smac:uri="add-url-prefix(protected/error/permission-denied)"/>

        </map:match> <!-- End of "protected/data/document-index/type-name/*" matcher -->

        <!-- Pseudo-Document list -->
        <map:match pattern="protected/data/pseudo-document-index/type-name/*">

          <!-- User group check -->
          <map:act type="check-user-groups">
            <map:parameter name="user-groups" smac:value="ugr-names-to-ids(authors,lecturers,admins)"/>

            <map:generate type="pseudo-document-index">
              <map:parameter name="type-name" value="{../1}"/>
              <map:parameter name="use-mode-name" value="{request-param:use-mode}"/>
              <map:parameter name="only-latest" value="{request-param:only-latest}"/>
            </map:generate>
            <map:serialize type="xml" />

          </map:act> <!-- End of check-user-groups action -->

          <!-- Redirect to error page -->
          <map:redirect-to smac:uri="add-url-prefix(protected/error/permission-denied)"/>

        </map:match> <!-- End of "protected/data/pseudo-document-index/type-name/*" matcher -->

        <!-- Number of sessions, as plain text -->
        <map:match pattern="protected/data/session-count">
          <map:read type="session-count"/>
        </map:match>

        <!-- ============================================================================ -->
        <!-- h2: Context "data", problem datasheets                                       -->
        <!-- ============================================================================ -->

        <!-- Corrected answers of a problem -->
        <map:match pattern="protected/data/problem-correction">

          <!-- User group check -->
          <map:act type="check-user-groups">
            <map:parameter name="user-groups"
                           smac:value="ugr-names-to-ids(tutors,lecturers,admins)"/>
            <map:act type="provide-worksheet-infos">
              <map:parameter name="ref" value="{request-param:ref}"/>
              <map:select type="simple">
                <map:parameter name="value" value="{timeframe-relation}"/>
                <map:when test="after">
                  <map:generate type="problem-datasheet">
                    <map:parameter name="ref" value="{request-param:ref}"/>
                    <map:parameter name="problem" value="{request-param:problem}"/>
                    <map:parameter name="user" value="{request-param:user}"/>
                    <map:parameter name="with-correction" value="true"/>
                  </map:generate>
                  <map:serialize type="xml"/>
                </map:when>
                <map:otherwise>
                  <map:read type="string">
                    <map:parameter name="string" value="ERROR: Timeframe has not ended yet"/>
                  </map:read>
                </map:otherwise>
              </map:select>
            </map:act>
            
          </map:act> <!-- End of check-user-groups action -->

          <!-- Redirect to error page -->
          <map:redirect-to smac:uri="add-url-prefix(protected/error/permission-denied)"/>

        </map:match> <!-- End of "protected/data/problem-correction" matcher -->

        <!-- Problem data (for applet) -->
        <map:match pattern="protected/data/problem">
          <map:generate type="problem-datasheet">
            <map:parameter name="ref" value="{request-param:ref}"/>
            <map:parameter name="problem" value="{request-param:problem}"/>
            <map:parameter name="remove-solutions" value="yes"/>
          </map:generate>
          <map:serialize type="xml"/>
        </map:match>

        <!-- ============================================================================ -->
        <!-- h2: Context "data", grades                                                   -->
        <!-- ============================================================================ -->

        <map:match pattern="protected/data/tutorial-user-problem-grades">

          <!-- Check if the user is a staff member: -->
          <map:act type="check-if-staff">
            <map:parameter name="course" value="{request-param:course}"/>

            <map:generate type="tutorial-user-problem-grades">
              <map:parameter name="tutorial" value="{request-param:tutorial}"/>
              <map:parameter name="course" value="{request-param:course}"/>
            </map:generate>
            <map:serialize type="xml"/>

          </map:act>

          <!-- Redirect to error page -->
          <map:redirect-to smac:uri="add-url-prefix(protected/error/permission-denied)"/>

        </map:match>

        <map:match pattern="protected/data/tutorial-user-traditional-problem-grades">

          <!-- Check if the user is a staff member: -->
          <map:act type="check-if-staff">
            <map:parameter name="course" value="{request-param:course}"/>

            <map:generate type="tutorial-user-traditional-problem-grades">
              <map:parameter name="tutorial" value="{request-param:tutorial}"/>
              <map:parameter name="course" value="{request-param:course}"/>
              <map:parameter name="store" value="{request-param:store}"/>
            </map:generate>
            <map:serialize type="xml"/>

          </map:act>

          <!-- Redirect to error page -->
          <map:redirect-to smac:uri="add-url-prefix(protected/error/permission-denied)"/>

        </map:match>

        <map:match pattern="protected/data/user-problem-grades">

          <!-- Check if the user is a staff member: -->
          <map:act type="check-if-staff">
            <map:parameter name="course" value="{request-param:course}"/>

            <map:generate type="user-problem-grades">
              <map:parameter name="user" value="{request-param:user}"/>
              <map:parameter name="course" value="{request-param:course}"/>
            </map:generate>
            <map:serialize type="xml"/>

          </map:act>

          <!-- Redirect to error page -->
          <map:redirect-to smac:uri="add-url-prefix(protected/error/permission-denied)"/>

        </map:match>

        <map:match pattern="protected/data/worksheet-user-problem-grades">

          <!-- Check if the user is a staff member: -->
          <map:act type="check-if-staff">
            <map:parameter name="course" value="{request-param:course}"/>

            <map:generate type="worksheet-user-problem-grades">
              <map:parameter name="user" value="{request-param:user}"/>
              <map:parameter name="worksheet" value="{request-param:worksheet}"/>
            </map:generate>
            <map:serialize type="xml"/>

          </map:act>

          <!-- Redirect to error page -->
          <map:redirect-to smac:uri="add-url-prefix(protected/error/permission-denied)"/>

        </map:match>

        <map:match pattern="protected/data/total-class-grades">

          <!-- User group check -->
          <map:act type="check-user-groups">
            <map:parameter name="user-groups"
                           smac:value="ugr-names-to-ids(tutors,lecturers,admins,syncs)"/>

            <map:generate type="total-class-grades">
              <map:parameter name="sync-id" value="{request-param:sync-id}"/>
            </map:generate>
            <map:serialize type="xml"/>

          </map:act>
          <!-- Redirect to error page -->
          <map:redirect-to smac:uri="add-url-prefix(protected/error/permission-denied)"/>

        </map:match>

        <map:match pattern="protected/data/class-user-worksheet-grades">

          <!-- User group check -->
          <map:act type="check-user-groups">
            <map:parameter name="user-groups"
                           smac:value="ugr-names-to-ids(tutors,lecturers,admins,syncs)"/>

            <map:generate type="class-user-worksheet-grades">
              <map:parameter name="class" value="{request-param:class}"/>
              <map:parameter name="class-sync-id" value="{request-param:class-sync-id}"/>
            </map:generate>
            <map:serialize type="xml"/>

          </map:act>
          <!-- Redirect to error page -->
          <map:redirect-to smac:uri="add-url-prefix(protected/error/permission-denied)"/>

        </map:match>

        <!-- ============================================================================ -->
        <!-- h2: Context "store"                                                          -->
        <!-- ============================================================================ -->

        <!-- =========================== -->
        <!-- h3: Storing problem answers -->
        <!-- =========================== -->

        <map:match pattern="protected/store/problem-answers">
          <map:act type="provide-worksheet-infos">
            <map:parameter name="ref" value="{request-param:ref}"/>
            <!-- This action provides the following sitemap parameters used below
            (it sets some more, but only these are used):
              case-store-answers
                  Distinguishes the following cases:
                  (1) User is a staff member,
                  (2) user is a course member and current time is before timeframe,
                  (3) user is a course member and current time is inside timframe,
                  (4) user is a course member and current time is after timframe,
                  (5) user is not a course member.
            -->

            <map:select type="simple">
              <map:parameter name="value" value="{case-store-answers}"/>

              <!-- ======================== -->
              <!-- h4: Case 1: Staff member -->
              <!-- ======================== -->
              
              <map:when test="staff">
                <map:read type="store-problem-answers">
                  <map:parameter name="ref" value="{request-param:ref}"/>
                  <map:parameter name="problem" value="{request-param:problem}"/>
                  <map:parameter name="issue-receipt" value="false"/>
                </map:read>

              </map:when>

              <!-- =========================================== -->
              <!-- h4: Case 2: Course member; before timeframe -->
              <!-- =========================================== -->

              <map:when test="student-before-timeframe">
                <map:act type="add-response-headers">
                  <map:parameter name="X-Mumie-Status" value="ERROR: Before timeframe"/>
                  <map:read type="string">
                    <map:parameter name="string" value="ERROR: Before timeframe"/>
                  </map:read>
                </map:act>
              </map:when>

              <!-- =========================================== -->
              <!-- h4: Case 3: Course member; inside timeframe -->
              <!-- =========================================== -->
              
              <map:when test="student-inside-timeframe">
                <map:read type="store-problem-answers">
                  <map:parameter name="ref" value="{request-param:ref}"/>
                  <map:parameter name="problem" value="{request-param:problem}"/>
                  <map:parameter name="issue-receipt" value="true"/>
                  <map:parameter name="send-receipt-with-response" value="{request-param:send-receipt-with-response}"/>
                </map:read>
              </map:when>

              <!-- =========================================== -->
              <!-- h4: Case 4: Course member; after timeframe  -->
              <!-- =========================================== -->

              <map:when test="student-after-timeframe">
                <map:act type="add-response-headers">
                  <map:parameter name="X-Mumie-Status" value="ERROR: After timeframe"/>
                  <map:read type="string">
                    <map:parameter name="string" value="ERROR: After timeframe"/>
                  </map:read>
                </map:act>
              </map:when>

              <!-- =============================== -->
              <!-- h4: Case 5: Not a course member -->
              <!-- ================================ -->

              <map:when test="other">
                <map:act type="add-response-headers">
                  <map:parameter name="X-Mumie-Status" value="ERROR: Not a course member"/>
                  <map:read type="string">
                    <map:parameter name="string" value="ERROR: Not a course member"/>
                  </map:read>
                </map:act>
              </map:when>

            </map:select>

          </map:act> <!-- End of "provide-worksheet-infos" action -->
        </map:match>


        <!-- ==================================== -->
        <!-- h3: Storing selftest problem answers -->
        <!-- ==================================== -->

        <map:match pattern="protected/store/selftest-problem-answers">
          <map:act type="provide-selftest-infos">
            <map:parameter name="ref" value="{request-param:ref}"/>
            <!-- This action provides the following sitemap parameters used below
            (it sets some more, but only these are used):
              case-store-answers
                  Distinguishes the following cases:
                  (1) User is a staff member,
                  (2) user is a course member and the worksheet state is "work",
                  (3) user is a course member and the worksheet state is "feedback"
             -->

            <map:select type="simple">
              <map:parameter name="value" value="{case-store-answers}"/>

              <!-- ======================== -->
              <!-- h4: Case 1: Staff member -->
              <!-- ======================== -->

              <map:when test="staff">
                <map:read type="store-problem-answers">
                  <map:parameter name="ref" value="{request-param:ref}"/>
                  <map:parameter name="problem" value="{request-param:problem}"/>
                  <map:parameter name="issue-receipt" value="false"/>
                </map:read>
              </map:when>

              <!-- ========================================================= -->
              <!-- h4: Case 2: Course member; inside timeframe; state "work" -->
              <!-- ========================================================= -->

              <map:when test="student-work-inside-timeframe">
                <map:read type="store-problem-answers">
                  <map:parameter name="ref" value="{request-param:ref}"/>
                  <map:parameter name="problem" value="{request-param:problem}"/>
                  <map:parameter name="issue-receipt" value="false"/>
                </map:read>
              </map:when>

              <!-- ============================================ -->
              <!-- h4: Case 3: Course member; outside timeframe -->
              <!-- ============================================ -->

              <map:when test="student-outside-timeframe">
                <map:act type="add-response-headers">
                  <map:parameter name="X-Mumie-Status"
                                 value="ERROR: Outside timeframe"/>
                  <map:read type="string">
                    <map:parameter name="string"
                                   value="ERROR: Outside timeframe"/>
                  </map:read>
                </map:act>
              </map:when>

              <!-- ============================================== -->
              <!-- h4: Case 4: Course member; state is "feedback" -->
              <!-- ============================================== -->

              <map:when test="student-feedback">
                <map:act type="add-response-headers">
                  <map:parameter name="X-Mumie-Status"
                                 value="ERROR: Worksheet is in feedback state"/>
                  <map:read type="string">
                    <map:parameter name="string"
                                   value="ERROR: Worksheet is in feedback state"/>
                  </map:read>
                </map:act>
              </map:when>

            </map:select>

          </map:act> <!-- End of "provide-selftest-infos" action -->
        </map:match>

        <!-- ==================================== -->
        <!-- h3: Popup window to store answers    -->
        <!-- ==================================== -->

        <map:match pattern="protected/store/problem-answers-dialog">
          <map:redirect-to
            smac:uri="resolve-path(
                        cocoon:/protected/view/document/type-name/generic_page/id/%{id},
                        system/problem/g_pge_save_answers_dialog.meta.xml,
                        generic_page)"/>
        </map:match>

        <map:match pattern="protected/store/problem-answers-reseipt/*">
          <map:act type="check-receipt">
                  <map:parameter name="receipt-filename"
                                 value="{1}"/>
            <map:read type="resource" src="{receipt-path}"/>
          </map:act>
          <map:redirect-to
           smac:uri="resolve-path(
                        cocoon:/protected/view/document/type-name/generic_page/id/%{id},
                        system/problem/g_pge_missing_receipt_message.meta.xml,
                        generic_page)"/>
        </map:match>


        <!-- ============================================================================ -->
        <!-- h2: Context "homework"                                                       -->
        <!-- ============================================================================ -->

        <map:match pattern="protected/homework/document/type-name/*/id/*">

          <!-- Read permission check -->
          <map:act type="check-read-permission">
            <map:parameter name="type-name" value="{1}"/>
            <map:parameter name="id" value="{2}"/>

            <!-- ============ -->
            <!-- h3: Problems -->
            <!-- ============ -->

            <map:match pattern="protected/homework/document/type-name/problem/id/*">
              <map:act type="provide-worksheet-infos">
                <map:parameter name="ref" value="{request-param:ref}"/>
                <!-- This action provides the following sitemap parameters used below
                (it sets some more, but only these are used):
                  case
                      Distinguishes the following cases that may occur with this URL:
                      (1) user is staff member, 
                      (2) user is a course member and current time is before timeframe,
                      (3) user is a course member and current time is inside or after
                          timframe,
                      (4) user is not a course member
                  course
                      Id of the course
                  worksheet
                      Id  of the worksheet
                  category
                      Category of the worksheet
                  is-course-member
                      Whether the user is a member of the course
                  timeframe-relation
                      Whether we are before, inside, or after the timeframe
                  after-timeframe
                      Whether we are after the timeframe
                  is-default-course
                      Whether course is default course   
                -->


                  <map:select type="simple">
                    <map:parameter name="value" value="{case}"/>
  
                    <!-- ======================== -->
                    <!-- h4: Case 1: Staff member -->
                    <!-- ======================== -->
  
                    <map:when test="staff">
                      <map:act type="setup-problem-for-staff">
                        <map:parameter name="student" value="{request-param:student}"/>
                        <map:parameter name="ref" value="{request-param:ref}"/>
                        <map:parameter name="problem" value="{../1}"/>
                        <map:parameter name="timeframe-relation" value="{timeframe-relation}"/>
                        <map:parameter name="clear-data" value="{request-param:clear-data}"/>
                        <map:parameter name="points" value="{request-param:points}"/>
                        <map:parameter name="correct" value="{request-param:correct}"/>
                        <!-- This action sets the following sitemap parameters used below:
                          tutor-view
                              Whether tutor view is enabled
                          student-selection-failed
                              Whether the selection of a student failed
                          student-selection-error-message
                              Error message in case the selection of a student failed
                          setting-points-failed
                              Whether the setting of the point number failed
                          setting-points-error-message
                              Error message in case the setting of the point number failed
                          user
                              The id of the user whose problem data should be loaded
                          include-correction
                              Whether the correction should be added
                          corrected
                              Whether the action triggered a correction of the user answers
                        -->
  
                        <map:generate label="generator">
                          <map:parameter name="type-name" value="problem"/>
                          <map:parameter name="id" value="{../../1}"/>
                          <map:parameter name="from-doc-type-name" value="worksheet"/>
                        </map:generate>
  
                        <map:transform type="add-params">
                          <map:parameter name="course" value="{../course}"/>
                          <map:parameter name="worksheet" value="{../worksheet}"/>
                          <map:parameter name="worksheet-category" value="{../category}"/>
                          <map:parameter name="is-staff-member" value="yes"/>
                          <map:parameter name="tutor-view" value="{tutor-view}"/>
                          <map:parameter name="student-selection-failed"
                                        value="{student-selection-failed}"/>
                          <map:parameter name="student-selection-error-message"
                                        value="{student-selection-error-message}"/>
                          <map:parameter name="setting-points-failed"
                                        value="{setting-points-failed}"/>
                          <map:parameter name="setting-points-error-message"
                                        value="{setting-points-error-message}"/>
                          <map:parameter name="corrected"
                                        value="{corrected}"/>
                          <map:parameter name="timeframe-relation"
                                        value="{../timeframe-relation}"/>
                          <map:parameter name="session-user" value="{session-attr:user}"/>
                          <map:parameter name="as-frame" value="{request-param:as-frame}"/>
                          <map:parameter name="is-default-course" value="{../is-default-course}"/>
                        </map:transform>
                        
                        <map:transform type="add-user">
                          <map:parameter name="id" value="{user}"/>
                        </map:transform>
  
                        <map:transform type="add-problem-datasheet" label="xml">
                          <map:parameter name="ref" value="{request-param:ref}"/>
                          <map:parameter name="problem" value="{../../1}"/>
                          <map:parameter name="with-correction" value="{include-correction}"/>
                          <map:parameter name="with-common-data" value="{include-correction}"/>
                          <map:parameter name="user" value="{user}"/>
                          <map:parameter name="time-format" value="dd.MM.yyyy HH:mm"/>
                        </map:transform>
  
                        <map:transform type="add-reference">
                          <map:parameter name="from-doc-type" smac:value="doctype-name-to-code(worksheet)"/>
                          <map:parameter name="to-doc-type" smac:value="doctype-name-to-code(generic_problem)"/>
                          <map:parameter name="id" value="{request-param:ref}"/>
                        </map:transform>
  
                        <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/problem/g_xsl_problem.meta.xml,generic_xsl_stylesheet)"/>
                        <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/xsl_stylesheet/id/%{id},system/common/xsl_math_signfix.meta.xml,xsl_stylesheet)"/>
  
                        <map:call resource="xhtml"/>
  
                      </map:act> <!-- End of action "setup-problem-for-staff" -->
                    </map:when> <!-- End of case 1 -->
  
                    <!-- =========================================== -->
                    <!-- h4: Case 2: Course member; before timeframe -->
                    <!-- =========================================== -->
  
                    <map:when test="student-before-timeframe">
                      <map:redirect-to smac:uri="resolve-path(%{prefix}/protected/view/document/type-name/generic_page/id/%{id}, system/problem/g_pge_before_timeframe.meta.xml, generic_page)"/>
                    </map:when>
  
                    <!-- ==================================================== -->
                    <!-- h4: Case 3: Course member; inside or after timeframe -->
                    <!-- ==================================================== -->
  
                    <map:when test="student-not-before-timeframe">
  
                      <map:generate label="generator">
                        <map:parameter name="type-name" value="problem"/>
                        <map:parameter name="id" value="{../1}"/>
                        <map:parameter name="from-doc-type" smac:value="doctype-name-to-code(worksheet)"/>
                      </map:generate>
  
                      <map:transform type="add-reference">
                        <map:parameter name="from-doc-type" smac:value="doctype-name-to-code(worksheet)"/>
                        <map:parameter name="to-doc-type" smac:value="doctype-name-to-code(generic_problem)"/>
                        <map:parameter name="id" value="{request-param:ref}"/>
                      </map:transform>
  
                      <map:transform type="add-params">
                        <map:parameter name="course" value="{course}"/>
                        <map:parameter name="worksheet" value="{worksheet}"/>
                        <map:parameter name="worksheet-category" value="{category}"/>
                        <map:parameter name="is-course-member" value="{is-course-member}"/>
                        <map:parameter name="timeframe-relation" value="{timeframe-relation}"/>
                        <map:parameter name="session-user" value="{session-attr:user}"/>
                        <map:parameter name="as-frame" value="{request-param:as-frame}"/>
                      </map:transform>
  
                      <map:transform type="add-user"/>
  
                      <map:transform type="add-problem-datasheet">
                                    <map:parameter name="ref" value="{request-param:ref}"/>
                                    <map:parameter name="problem" value="{../1}"/>
                                    <map:parameter name="with-correction" value="{../after-timeframe}"/>
                                    <map:parameter name="with-common-data" value="{../after-timeframe}"/>
                        <map:parameter name="time-format" value="dd.MM.yyyy HH:mm"/>
                      </map:transform>
  
                      <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/problem/g_xsl_problem.meta.xml,generic_xsl_stylesheet)"/>
                      <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/xsl_stylesheet/id/%{id},system/common/xsl_math_signfix.meta.xml,xsl_stylesheet)" label="xml"/>
  
                      <map:call resource="xhtml"/>
                    </map:when>
  
                    <!-- ================================ -->
                    <!-- h4: Case 4: Not a course member  -->
                    <!-- ================================ -->
  
                    <map:when test="other">
                      <map:redirect-to smac:uri="resolve-path(%{prefix}/protected/view/document/type-name/generic_page/id/%{id}, system/problem/g_pge_not_a_course_member.meta.xml, generic_page)"/>
                    </map:when>
                    
                  </map:select>

              </map:act> <!-- End of action "provide-worksheet-infos" -->

            </map:match> <!-- End of match "protected/homework/document/type-name/problem/id/*" -->

            <!-- ============= -->
            <!-- h3: Worksheet -->
            <!-- ============= -->

            <map:match pattern="protected/homework/document/type-name/worksheet/id/*">
              <map:act type="provide-worksheet-infos">
                <map:parameter name="worksheet" value="{1}"/>
                <map:parameter name="time-format" value="dd.MM.yyyy HH:mm"/>
                <!-- This action provides the following sitemap parameters used below
                (it sets some more, but only these are used):
                  case
                      Distinguishes the following cases that may occur with this URL:
                      (1) user is staff member, 
                      (2) user is a course member and current time is before timeframe,
                      (3) user is a course member and current time is inside or after
                          timframe,
                      (4) user is not a course member
                  course
                      Id of the course
                  is-course-member
                      Whether the current user is a course member
                  worksheet
                      Id  of the worksheet
                  category
                      Category of the worksheet
                  label
                      Label of the worksheet
                  summary
                      Id of the generic summary
                  timeframe-start
                      Beginning of the timeframe
                  timeframe-start-raw
                      Beginning of the timeframe, as seconds since 00:00 Jan 01 1970
                  timeframe-end
                      End of the timeframe
                  timeframe-end-raw
                      End of the timeframe, as seconds since 00:00 Jan 01 1970
                -->



                <map:select type="simple">
                  <map:parameter name="value" value="{case}"/>

                  <!-- ======================== -->
                  <!-- h4: Case 1: Staff member -->
                  <!-- ======================== -->

                  <map:when test="staff">

                    <map:act type="setup-worksheet-for-staff">
                      <map:parameter name="student" value="{request-param:student}"/>
                      <map:parameter name="timeframe-relation" value="{timeframe-relation}"/>
                      <!-- This action sets the following sitemap parameters used below:
                        tutor-view
                            Whether tutor view is enabled
                        user
                            The id of the user whose problem data should be loaded
                        student-selection-failed
                            Whether the selection of a student failed
                        student-selection-error-message
                            Error message in case the selection of a student failed
                      -->

                    <map:act type="resolve-generic-document">
                      <map:parameter name="name-of-generic" value="generic_summary"/>
                      <map:parameter name="id-of-generic" value="{../summary}"/>
                      <map:parameter name="language" value="{session-attr:language}"/>
                      <map:parameter name="theme" value="{session-attr:theme}"/>

                      <map:generate type="document">
                        <map:parameter name="type-name" value="summary"/>
                        <map:parameter name="id" value="{id-of-real}"/>
                      </map:generate>

                      <map:transform type="add-params">
                        <map:parameter name="course" value="{../../course}"/>
                        <map:parameter name="worksheet" value="{../../worksheet}"/>
                        <map:parameter name="worksheet-category" value="{../../category}"/>
                        <map:parameter name="worksheet-label" value="{../../label}"/>
                        <map:parameter name="is-staff-member" value="yes"/>
                        <map:parameter name="timeframe-relation" value="{../../timeframe-relation}"/>
                        <map:parameter name="timeframe-start" value="{../../timeframe-start}"/>
                        <map:parameter name="timeframe-start-raw" value="{../../timeframe-start-raw}"/>
                        <map:parameter name="timeframe-end" value="{../../timeframe-end}"/>
                        <map:parameter name="timeframe-end-raw" value="{../../timeframe-end-raw}"/>
                        <map:parameter name="session-user" value="{session-attr:user}"/>
                        <map:parameter name="tutor-view" value="{../tutor-view}"/>
                        <map:parameter name="student-selection-failed" value="{../student-selection-failed}"/>
                        <map:parameter name="student-selection-error-message" value="{../student-selection-error-message}"/>
                        <map:parameter name="with-load-button" value="{request-param:with-load-button}"/>
                        <map:parameter name="as-frame" value="{request-param:as-frame}"/>
                      </map:transform>
                    
                      <map:transform type="add-user">
                        <map:parameter name="id" value="{../user}"/>
                      </map:transform>

                      <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme}, system/course/g_xsl_worksheet_summary.meta.xml, generic_xsl_stylesheet)"/>
                      <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/xsl_stylesheet/id/%{id}, system/common/xsl_math_signfix.meta.xml, xsl_stylesheet)"/>

                      <map:call resource="xhtml"/>

                      </map:act> <!-- End of resolve-generic-document action -->
                    </map:act> <!-- End of setup-worksheet-for-staff action -->

                  </map:when> <!-- End of case 1 -->

                  <!-- =========================================== -->
                  <!-- h4: Case 2: Course member; before timeframe -->
                  <!-- =========================================== -->

                  <map:when test="student-before-timeframe">
                    <map:redirect-to smac:uri="resolve-path(%{prefix}/protected/view/document/type-name/generic_page/id/%{id}, system/course/g_pge_worksheet_before_timeframe.meta.xml, generic_page)"/>
                  </map:when>

                  <!-- ==================================================== -->
                  <!-- h4: Case 3: Course member; inside or after timeframe -->
                  <!-- ==================================================== -->

                  <map:when test="student-not-before-timeframe">

                  <map:act type="resolve-generic-document">
                    <map:parameter name="name-of-generic" value="generic_summary"/>
                    <map:parameter name="id-of-generic" value="{summary}"/>
                    <map:parameter name="language" value="{session-attr:language}"/>
                    <map:parameter name="theme" value="{session-attr:theme}"/>

                    <map:generate type="document">
                      <map:parameter name="type-name" value="summary"/>
                      <map:parameter name="id" value="{id-of-real}"/>
                    </map:generate>

                    <map:transform type="add-params">
                      <map:parameter name="course" value="{../course}"/>
                      <map:parameter name="worksheet" value="{../worksheet}"/>
                      <map:parameter name="worksheet-category" value="{../category}"/>
                      <map:parameter name="worksheet-label" value="{../label}"/>
                      <map:parameter name="is-course-member" value="{../is-course-member}"/>
                      <map:parameter name="timeframe-relation" value="{../timeframe-relation}"/>
                      <map:parameter name="timeframe-start" value="{../timeframe-start}"/>
                      <map:parameter name="timeframe-start-raw" value="{../timeframe-start-raw}"/>
                      <map:parameter name="timeframe-end" value="{../timeframe-end}"/>
                      <map:parameter name="timeframe-end-raw" value="{../timeframe-end-raw}"/>
                      <map:parameter name="session-user" value="{session-attr:user}"/>
                      <map:parameter name="with-load-button" value="{request-param:with-open-button}"/>
                      <map:parameter name="as-frame" value="{request-param:as-frame}"/>
                    </map:transform>
                    
                    <map:transform type="add-user"/>

                    <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme}, system/course/g_xsl_worksheet_summary.meta.xml, generic_xsl_stylesheet)"/>
                    <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/xsl_stylesheet/id/%{id}, system/common/xsl_math_signfix.meta.xml, xsl_stylesheet)"/>

                    <map:call resource="xhtml"/>

                  </map:act> <!-- End of resolve-generic-document action -->
                  
                  </map:when>  <!-- End of case 3 -->

                  <!-- =============================== -->
                  <!-- h4: Case 4: Not a course member -->
                  <!-- =============================== -->

                  <map:when test="other">
                    <map:redirect-to smac:uri="resolve-path(%{prefix}/protected/view/document/type-name/generic_page/id/%{id}, system/course/g_pge_worksheet_not_a_course_member.meta.xml, generic_page)"/>
                  </map:when>
                  
                </map:select>
              </map:act> <!-- End of provide-worksheet-infos action -->

            </map:match> <!-- End of "protected/homework/document/type-name/worksheet/id/*" matcher -->
                
            <!-- ========== -->
            <!-- h3: Course -->
            <!-- ========== -->

            <map:match pattern="protected/homework/document/type-name/course/id/*">
              <map:act type="provide-course-infos">
                <map:parameter name="course" value="{1}"/>
                <map:parameter name="default-summary"
                  smac:value="resolve-path(
                                %{id},
                                system/course/g_sum_default.meta.xml,
                                generic_summary)"/>
                <!-- This action provides the following sitemap parameters used below
                (it sets some more, but only these are used):
                  case
                      Distinguishes the two cases that may occur with this URL:
                      (1) user is staff member, (2) user is student.
                  summary
                      Id of the generic summary
                  is-course-member
                      whether the user is a member of this courses elearning class
                  is-staff-member
                      whether the user is admin or a tutor or lecturer of this courses
                      e-learning class.
                -->


                <map:select type="simple">
                  <map:parameter name="value" value="{case}"/>

                  <!-- ================================ -->
                  <!-- h4: Case 1: User is staff member -->
                  <!-- ================================ -->

                  <map:when test="staff">

                    <map:act type="setup-course-for-staff">
                      <map:parameter name="student" value="{request-param:student}"/>
                      <!-- This action sets the following sitemap parameters used below:
                        tutor-view
                            Whether tutor view is enabled
                        user
                            The id of the user whose problem data should be loaded
                        student-selection-failed
                            Whether the selection of a student failed
                        student-selection-error-message
                            Error message in case the selection of a student failed
                      -->

                    <map:act type="resolve-generic-document">
                      <map:parameter name="name-of-generic" value="generic_summary"/>
                      <map:parameter name="id-of-generic" value="{../summary}"/>
                      <map:parameter name="language" value="{session-attr:language}"/>
                      <map:parameter name="theme" value="{session-attr:theme}"/>

                      <map:generate type="document">
                        <map:parameter name="type-name" value="summary"/>
                        <map:parameter name="id" value="{id-of-real}"/>
                      </map:generate>

                      <map:transform type="add-params">
                        <map:parameter name="course" value="{../../../1}"/>
                        <map:parameter name="session-user" value="{session-attr:user}"/>
                        <map:parameter name="with-load-button" value="{request-param:with-load-button}"/>
                        <map:parameter name="is-staff-member" value="true"/>
                        <map:parameter name="tutor-view" value="{../tutor-view}"/>
                        <map:parameter name="student-selection-failed" value="{../student-selection-failed}"/>
                        <map:parameter name="student-selection-error-message" value="{../student-selection-error-message}"/>
                        <map:parameter name="as-frame" value="{request-param:as-frame}"/>
                      </map:transform>
                    
                      <map:transform type="add-user">
                        <map:parameter name="id" value="{../user}"/>
                      </map:transform>

                      <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme}, system/course/g_xsl_course_summary.meta.xml, generic_xsl_stylesheet)"/>
                      <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/xsl_stylesheet/id/%{id}, system/common/xsl_math_signfix.meta.xml, xsl_stylesheet)"/>

                      <map:call resource="xhtml"/>

                    </map:act> <!-- End of resolve-generic-document action -->

                    </map:act> <!-- End of setup-worksheet-for-staff action -->

                  </map:when> <!-- End of case 1 -->

                  <!-- ============================= -->
                  <!-- h4: Case 2: Course member     -->
                  <!-- ============================= -->

                  <map:when test="student">

                    <map:act type="resolve-generic-document">
                      <map:parameter name="name-of-generic" value="generic_summary"/>
                      <map:parameter name="id-of-generic" value="{summary}"/>
                      <map:parameter name="language" value="{session-attr:language}"/>
                      <map:parameter name="theme" value="{session-attr:theme}"/>
  
                      <map:generate type="document">
                        <map:parameter name="type-name" value="summary"/>
                        <map:parameter name="id" value="{id-of-real}"/>
                      </map:generate>
  
                      <map:transform type="add-params">
                        <map:parameter name="course" value="{../../1}"/>
                        <map:parameter name="session-user" value="{session-attr:user}"/>
                        <map:parameter name="with-open-button" value="{request-param:with-open-button}"/>
                        <map:parameter name="is-course-member" value="yes"/>
                        <map:parameter name="as-frame" value="{request-param:as-frame}"/>
                      </map:transform>
                      
                      <map:transform type="add-user"/>
  
                      <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme}, system/course/g_xsl_course_summary.meta.xml, generic_xsl_stylesheet)"/>
                      <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/xsl_stylesheet/id/%{id}, system/common/xsl_math_signfix.meta.xml, xsl_stylesheet)"/>
                      
                      <map:call resource="xhtml"/>
  
                    </map:act> <!-- End of resolve-generic-document action -->
                  </map:when>  <!-- End of case 2 -->

                  <!-- =============================== -->
                  <!-- h4: Case 3: Not a course member -->
                  <!-- =============================== -->

                  <map:when test="other">
                    <map:redirect-to smac:uri="resolve-path(%{prefix}/protected/view/document/type-name/generic_page/id/%{id}, system/course/g_pge_not_a_course_member.meta.xml, generic_page)"/>
                  </map:when>  <!-- End of case 3 -->
                  
                </map:select>              

              </map:act> <!-- End of provide-course-infos action -->

            </map:match> <!-- End of "protected/homework/document/type-name/course/id/*" matcher -->
                
          </map:act> <!-- End of check-read-permission action -->

          <!-- Redirect to error page -->
          <map:redirect-to smac:uri="add-url-prefix(protected/error/permission-denied)"/>

        </map:match> <!-- End of "protected/homework/document/type-name/*/id/*" matcher -->

        <!-- ===================== -->
        <!-- h3: Corrected answers -->
        <!-- ===================== -->

        <map:match pattern="protected/homework/problem-correction">


          <map:act type="provide-worksheet-infos">
            <map:parameter name="ref" value="{request-param:ref}"/>
            <!-- This action provides the following sitemap parameters used below
            (it sets some more, but only these are used):
              case-view-correction
                  Distinguishes the following cases:
                  (1) User is a staff member,
                  (2) user is a course member and current time is after timframe,
                  (3) user is a course member and current time is before or inside
                      timframe and worksheet-category is selftest,
                  (4) user is a course member and current time is before or inside
                      timframe,
                  (5) user is not a course member.
              timeframe-relation
                  Whether we are before, inside, or after the timeframe
            -->

            <map:select type="simple">
              <map:parameter name="value" value="{case-view-correction}"/>

              <!-- ======================== -->
              <!-- h4: Case 1: Staff member -->
              <!-- ======================== -->

              <map:when test="staff">

                <map:act type="setup-problem-for-staff">
                  <map:parameter name="user" value="{request-param:user}"/>
                  <map:parameter name="ref" value="{request-param:ref}"/>
                  <map:parameter name="problem" value="{request-param:problem}"/>
                  <map:parameter name="timeframe-relation" value="{timeframe-relation}"/>
                  <map:parameter name="correct" value="{request-param:correct}"/>

                  <map:generate type="problem-datasheet">
                    <map:parameter name="user" value="{request-param:user}"/>
                    <map:parameter name="problem" value="{request-param:problem}"/>
                    <map:parameter name="ref" value="{request-param:ref}"/>
                    <map:parameter name="with-correction" value="true"/>
                  </map:generate>

                  <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme}, system/problem/g_xsl_problem_correction, generic_xsl_stylesheet)"/>

                  <map:call resource="xhtml"/>

                </map:act>

              </map:when>

              <!-- =========================================== -->
              <!-- h4: Case 2: Course member; after timeframe  -->
              <!-- =========================================== -->

              <map:when test="student-after-timeframe">

                <map:generate type="problem-datasheet">
                  <map:parameter name="ref" value="{request-param:ref}"/>
                  <map:parameter name="problem" value="{request-param:problem}"/>
                  <map:parameter name="with-correction" value="true"/>
                </map:generate>

                <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme}, system/problem/g_xsl_problem_correction, generic_xsl_stylesheet)"/>

                <map:call resource="xhtml"/>

              </map:when>

              <!-- ========================================================= -->
              <!-- h4: Case 3: Course member; not after timeframe, selftest  -->
              <!-- ========================================================= -->

              <map:when test="student-not-after-timeframe-selftest">

                <map:generate type="problem-datasheet">
                  <map:parameter name="ref" value="{request-param:ref}"/>
                  <map:parameter name="problem" value="{request-param:problem}"/>
                  <map:parameter name="with-correction" value="true"/>
                </map:generate>

                <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme}, system/problem/g_xsl_problem_correction, generic_xsl_stylesheet)"/>

                <map:call resource="xhtml"/>

              </map:when>


              <!-- ========================================================= -->
              <!-- h4: Case 4: Course member; not after timeframe, training  -->
              <!-- ========================================================= -->

              <map:when test="student-not-after-timeframe-training">

                <map:act type="setup-training-problem">
                  <map:parameter name="ref" value="{request-param:ref}"/>
                  <map:parameter name="problem" value="{request-param:problem}"/>
                  <map:parameter name="clear-data" value="{request-param:clear-data}"/>
                  <map:parameter name="correct" value="{request-param:correct}"/>

                  <map:generate type="problem-datasheet">
                    <map:parameter name="ref" value="{request-param:ref}"/>
                    <map:parameter name="problem" value="{request-param:problem}"/>
                    <map:parameter name="with-correction" value="true"/>
                  </map:generate>
  
                  <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme}, system/problem/g_xsl_problem_correction, generic_xsl_stylesheet)"/>
  
                  <map:call resource="xhtml"/>

                </map:act>

              </map:when>

              <!-- =============================================== -->
              <!-- h4: Case 5: Course member; not after timeframe  -->
              <!-- =============================================== -->

              <map:when test="student-not-after-timeframe">

                <map:read type="string">
                  <map:parameter name="string" value="ERROR: Timeframe has not ended yet"/>
                </map:read>

              </map:when>

              <!-- =============================== -->
              <!-- h4: Case 6: Not a course member -->
              <!-- =============================== -->

              <map:when test="other">

                <map:read type="string">
                  <map:parameter name="string" value="ERROR: Not a course member"/>
                </map:read>

              </map:when>
              
            </map:select>

          </map:act> <!-- End of provide-worksheet-infos action -->

        </map:match>

        <!-- ============================================================================ -->
        <!-- h2: Context "selftest"                                                       -->
        <!-- ============================================================================ -->

        <map:match pattern="protected/selftest/document/type-name/*/id/*">

          <!-- Read permission check -->
          <map:act type="check-read-permission">
            <map:parameter name="type-name" value="{1}"/>
            <map:parameter name="id" value="{2}"/>

            <!-- ============ -->
            <!-- h3: Problems -->
            <!-- ============ -->

            <map:match pattern="protected/selftest/document/type-name/problem/id/*">
              <map:act type="provide-selftest-infos">
                <map:parameter name="ref" value="{request-param:ref}"/>
                <!-- This action provides the following sitemap parameters used below
                (it sets some more, but only these are used):
                  case
                      Distinguishes the following cases that may occur with this URL:
                      (1) user is a staff member, or user is a course member and
                          current time is inside timeframe,
                      (2) user is a course member and current time is outside timeframe,
                      (3) user is neither a staff nor a course member.
                  course
                      Id of the course
                  worksheet
                      Id  of the worksheet
                  is-course-member
                      Whether the user is a member of the course
                  state
                      The state of the worksheet
                  timeframe-relation
                      Whether we are before, inside, or after the timeframe
                  in-feedback-state
                      Whether the worksheet is in in the state "feedback"
                -->

                <map:select type="simple">
                  <map:parameter name="value" value="{case}"/>

                  <!-- ======================== -->
                  <!-- h4: Case 1: Staff member -->
                  <!-- ======================== -->

                  <map:when test="staff">
                    <map:act type="setup-selftest-problem-for-staff">
                      <map:parameter name="ref" value="{request-param:ref}"/>
                      <map:parameter name="problem" value="{../1}"/>
                      <map:parameter name="worksheet-state" value="{state}"/>
                      <map:parameter name="clear-data" value="{request-param:clear-data}"/>
                      <map:parameter name="correct" value="{request-param:correct}"/>
                      <!-- This action sets the following sitemap parameters used below:
                        include-correction
                            Whether the correction should be added
                        corrected
                            Whether the action triggered a correction of the user answers
                      -->

                      <map:generate label="generator">
                        <map:parameter name="type-name" value="problem"/>
                        <map:parameter name="id" value="{../../1}"/>
                        <map:parameter name="from-doc-type" smac:value="doctype-name-to-code(worksheet)"/>
                      </map:generate>

                      <map:transform type="add-reference">
                        <map:parameter name="from-doc-type" smac:value="doctype-name-to-code(worksheet)"/>
                        <map:parameter name="to-doc-type" smac:value="doctype-name-to-code(generic_problem)"/>
                        <map:parameter name="id" value="{request-param:ref}"/>
                      </map:transform>

                      <map:transform type="add-params">
                        <map:parameter name="course" value="{../course}"/>
                        <map:parameter name="worksheet" value="{../worksheet}"/>
                        <map:parameter name="worksheet-category" value="{../category}"/>
                        <map:parameter name="is-staff-member" value="yes"/>
                        <map:parameter name="state" value="{../state}"/>
                        <map:parameter name="corrected" value="{corrected}"/>
                        <map:parameter name="timeframe-relation" value="{../timeframe-relation}"/>
                        <map:parameter name="session-user" value="{session-attr:user}"/>
                        <map:parameter name="as-frame" value="{request-param:as-frame}"/>
                      </map:transform>

                      <map:transform type="add-user"/>

                      <map:transform type="add-problem-datasheet">
                        <map:parameter name="ref" value="{request-param:ref}"/>
                        <map:parameter name="problem" value="{../../1}"/>
                        <map:parameter name="with-correction" value="{include-correction}"/>
                        <map:parameter name="with-common-data" value="{include-correction}"/>
                        <map:parameter name="time-format" value="dd.MM.yyyy HH:mm"/>
                      </map:transform>

                      <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/problem/g_xsl_problem.meta.xml,generic_xsl_stylesheet)"/>
                      <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/xsl_stylesheet/id/%{id},system/common/xsl_math_signfix.meta.xml,xsl_stylesheet)" label="xml"/>

                      <map:call resource="xhtml"/>

                    </map:act> <!-- End of action "setup-selftest-problem-for-staff" -->
                  </map:when>

                  <!-- ==================================================== -->
                  <!-- h4: Case 2: Course member; inside or after timeframe -->
                  <!-- ==================================================== -->

                  <map:when test="student-inside-or-after-timeframe">

                    <map:generate label="generator">
                      <map:parameter name="type-name" value="problem"/>
                      <map:parameter name="id" value="{../1}"/>
                      <map:parameter name="from-doc-type" smac:value="doctype-name-to-code(worksheet)"/>
                    </map:generate>

                    <map:transform type="add-reference">
                      <map:parameter name="from-doc-type" smac:value="doctype-name-to-code(worksheet)"/>
                      <map:parameter name="to-doc-type" smac:value="doctype-name-to-code(generic_problem)"/>
                      <map:parameter name="id" value="{request-param:ref}"/>
                    </map:transform>

                    <map:transform type="add-params">
                      <map:parameter name="course" value="{course}"/>
                      <map:parameter name="worksheet" value="{worksheet}"/>
                      <map:parameter name="worksheet-category" value="{category}"/>
                      <map:parameter name="worksheet-state" value="{state}"/>
                      <map:parameter name="is-course-member" value="{is-course-member}"/>
                      <map:parameter name="state" value="{state}"/>
                      <map:parameter name="timeframe-relation" value="{timeframe-relation}"/>
                      <map:parameter name="session-user" value="{session-attr:user}"/>
                      <map:parameter name="as-frame" value="{request-param:as-frame}"/>
                    </map:transform>

                    <map:transform type="add-user"/>

                    <map:transform type="add-problem-datasheet">
                      <map:parameter name="ref" value="{request-param:ref}"/>
                      <map:parameter name="problem" value="{../1}"/>
                      <map:parameter name="with-correction" value="{in-feedback-state}"/>
                      <map:parameter name="with-common-data" value="{in-feedback-state}"/>
                      <map:parameter name="time-format" value="dd.MM.yyyy HH:mm"/>
                    </map:transform>

                    <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/problem/g_xsl_problem.meta.xml,generic_xsl_stylesheet)"/>
                    <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/xsl_stylesheet/id/%{id},system/common/xsl_math_signfix.meta.xml,xsl_stylesheet)" label="xml"/>

                    <map:call resource="xhtml"/>
                  </map:when>

                  <!-- =========================================== -->
                  <!-- h4: Case 3: Course member; before timeframe -->
                  <!-- =========================================== -->

                  <map:when test="student-before-timeframe">
                    <map:redirect-to smac:uri="resolve-path(%{prefix}/protected/view/document/type-name/generic_page/id/%{id}, system/problem/g_pge_before_timeframe.meta.xml, generic_page)"/>
                  </map:when>

                  <!-- ========================================= -->
                  <!-- h4: Case 4: Not a staff or course member  -->
                  <!-- ========================================= -->

                  <map:when test="other">
                    <map:redirect-to smac:uri="resolve-path(%{prefix}/protected/view/document/type-name/generic_page/id/%{id}, system/problem/g_pge_not_a_course_member.meta.xml, generic_page)"/>
                  </map:when>
                  
                </map:select>
              </map:act> <!-- End of action "provide-selftest-infos" -->

            </map:match> <!-- End of "protected/selftest/document/type-name/problem/id/*" match -->

            <!-- ============== -->
            <!-- h3: Worksheets -->
            <!-- ============== -->

            <map:match pattern="protected/selftest/document/type-name/worksheet/id/*">
              <map:act type="prepare-selftest">
                <map:parameter name="worksheet" value="{1}"/>
                <map:parameter name="new-state" value="{request-param:new-state}"/>
                <map:parameter name="time-format" value="dd.MM.yyyy HH:mm"/>
                <!-- Does the following:
                (a) If necessary, changes the state in the database.
                (b) If the state has changed to "work", deletes PPDs, answers, and
                    corrections.
                (c) If the state has changed to feedback, sets the "correct" flag,
                    so that the worksheet-user-bulk-correction transformer is
                    enabled (see below).
                Sets the following sitemap parameters:
                  case
                      Distinguishes the following cases that may occur with this URL:
                      (1) user is a staff member, or user is a course member and
                          current time is inside timeframe,
                      (2) user is a course member and current time is outside timeframe,
                      (3) user is neither a staff nor a course member.
                  worksheet
                      Id  of the worksheet
                  category
                      Category of the worksheet
                  label
                      Label of the worksheet
                  summary
                      Id of the generic summary
                  timeframe-start
                      Beginning of the timeframe
                  timeframe-start-raw
                      Beginning of the timeframe, as seconds since 00:00 Jan 01 1970
                  timeframe-end
                      End of the timeframe
                  timeframe-end-raw
                      End of the timeframe, as seconds since 00:00 Jan 01 1970
                  is-course-member
                      Whether the user is a member of the course
                  state
                      The state of the worksheet
                  correct
                      Whether the problems of the worksheet must be corrected
                -->
                

                <map:select type="simple">
                  <map:parameter name="value" value="{case}"/>

                  <!-- =========================================================== -->
                  <!-- h4: Case 1: Staff member, or course member inside timeframe -->
                  <!-- =========================================================== -->

                  <map:when test="staff-or-student-inside-timeframe">
                    
                    <map:act type="resolve-generic-document">
                      <map:parameter name="name-of-generic" value="generic_summary"/>
                      <map:parameter name="id-of-generic" value="{summary}"/>
                      <map:parameter name="language" value="{session-attr:language}"/>
                      <map:parameter name="theme" value="{session-attr:theme}"/>

                      <map:generate type="document">
                        <map:parameter name="type-name" value="summary"/>
                        <map:parameter name="id" value="{id-of-real}"/>
                      </map:generate>

                          <map:transform type="add-params">
                        <map:parameter name="course" value="{../course}"/>
                        <map:parameter name="worksheet" value="{../worksheet}"/>
                        <map:parameter name="worksheet-category" value="{../category}"/>
                        <map:parameter name="worksheet-label" value="{../label}"/>
                        <map:parameter name="worksheet-state" value="{../state}"/>
                        <map:parameter name="session-user" value="{session-attr:user}"/>
                        <map:parameter name="is-staff-member" value="{../is-staff-member}"/>
                        <map:parameter name="timeframe-relation" value="{../timeframe-relation}"/>
                        <map:parameter name="timeframe-start" value="{../timeframe-start}"/>
                        <map:parameter name="timeframe-start-raw" value="{../timeframe-start-raw}"/>
                        <map:parameter name="timeframe-end" value="{../timeframe-end}"/>
                        <map:parameter name="timeframe-end-raw" value="{../timeframe-end-raw}"/>
                        <map:parameter name="is-course-member" value="{../is-course-member}"/>
                        <map:parameter name="with-load-button" value="{request-param:with-open-button}"/>
                        <map:parameter name="as-frame" value="{request-param:as-frame}"/>
                      </map:transform>
                      
                      <map:transform type="add-user"/>

                      <map:act type="string-equal">
                        <map:parameter name="value-1" value="{../correct}"/>
                        <map:parameter name="value-2" value="yes"/>
                        <map:transform type="worksheet-user-bulk-correction">
                          <map:parameter name="user" value="{session-attr:user}"/>
                          <map:parameter name="worksheet" value="{../../worksheet}"/>
                        </map:transform>
                      </map:act>

                      <map:act type="string-equal">
                        <map:parameter name="value-1" value="{../state}"/>
                        <map:parameter name="value-2" value="feedback"/>
                        <map:transform type="add-worksheet-user-problem-grades">
                          <map:parameter name="user" value="{session-attr:user}"/>
                          <map:parameter name="worksheet" value="{../../worksheet}"/>
                        </map:transform>
                      </map:act>

                      <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme}, system/course/g_xsl_worksheet_summary.meta.xml, generic_xsl_stylesheet)"/>
                      <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/xsl_stylesheet/id/%{id}, system/common/xsl_math_signfix.meta.xml, xsl_stylesheet)"/>

                      <map:call resource="xhtml"/>

                    </map:act> <!-- End of resolve-generic-document action -->
                  </map:when>

                  <!-- ============================================ -->
                  <!-- h4: Case 2: Course member, before timeframe -->
                  <!-- ============================================ -->

                  <map:when test="student-before-timeframe">
                    <map:redirect-to smac:uri="resolve-path(%{prefix}/protected/view/document/type-name/generic_page/id/%{id}, system/course/g_pge_worksheet_before_timeframe.meta.xml, generic_page)"/>
                  </map:when>

                  <!-- ============================================ -->
                  <!-- h4: Case 3: Course member, after timeframe -->
                  <!-- ============================================ -->

                  <map:when test="student-after-timeframe">
                    <map:act type="resolve-generic-document">
                      <map:parameter name="name-of-generic" value="generic_summary"/>
                      <map:parameter name="id-of-generic" value="{summary}"/>
                      <map:parameter name="language" value="{session-attr:language}"/>
                      <map:parameter name="theme" value="{session-attr:theme}"/>

                      <map:generate type="document">
                        <map:parameter name="type-name" value="summary"/>
                        <map:parameter name="id" value="{id-of-real}"/>
                      </map:generate>

                      <map:transform type="add-params">
                        <map:parameter name="course" value="{../course}"/>
                        <map:parameter name="worksheet" value="{../worksheet}"/>
                        <map:parameter name="worksheet-category" value="{../category}"/>
                        <map:parameter name="worksheet-label" value="{../label}"/>
                        <map:parameter name="worksheet-state" value="{../state}"/>
                        <map:parameter name="session-user" value="{session-attr:user}"/>
                        <map:parameter name="is-staff-member" value="{../is-staff-member}"/>
                        <map:parameter name="timeframe-relation" value="{../timeframe-relation}"/>
                        <map:parameter name="timeframe-start" value="{../timeframe-start}"/>
                        <map:parameter name="timeframe-start-raw" value="{../timeframe-start-raw}"/>
                        <map:parameter name="timeframe-end" value="{../timeframe-end}"/>
                        <map:parameter name="timeframe-end-raw" value="{../timeframe-end-raw}"/>
                        <map:parameter name="is-course-member" value="{../is-course-member}"/>
                        <map:parameter name="with-load-button" value="{request-param:with-open-button}"/>
                        <map:parameter name="as-frame" value="{request-param:as-frame}"/>
                      </map:transform>
                      
                      <map:transform type="add-user"/>

                      <map:act type="string-equal">
                        <map:parameter name="value-1" value="{../../correct}"/>
                        <map:parameter name="value-2" value="yes"/>
                        <map:transform type="worksheet-user-bulk-correction">
                          <map:parameter name="user" value="{session-attr:user}"/>
                          <map:parameter name="worksheet" value="{../../worksheet}"/>
                        </map:transform>
                      </map:act>

                      <map:transform type="add-worksheet-user-problem-grades">
                        <map:parameter name="user" value="{session-attr:user}"/>
                        <map:parameter name="worksheet" value="{../../worksheet}"/>
                      </map:transform>

                      <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme}, system/course/g_xsl_worksheet_summary.meta.xml, generic_xsl_stylesheet)"/>
                      <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/xsl_stylesheet/id/%{id}, system/common/xsl_math_signfix.meta.xml, xsl_stylesheet)"/>

                      <map:call resource="xhtml"/>

                    </map:act> <!-- End of resolve-generic-document action -->
                  </map:when>

                  <!-- ======================================== -->
                  <!-- h4: Case 4: Not a staff or course member -->
                  <!-- ======================================== -->

                  <map:when test="other">
                    <map:redirect-to smac:uri="resolve-path(%{prefix}/protected/view/document/type-name/generic_page/id/%{id}, system/course/g_pge_worksheet_not_a_course_member.meta.xml, generic_page)"/>
                  </map:when>

                </map:select>
              </map:act>
            </map:match> <!-- End of "protected/selftest/document/type-name/worksheet/id/*" match -->

          </map:act> <!-- End of check-read-permission action -->

          <!-- Redirect to error page -->
          <map:redirect-to smac:uri="add-url-prefix(protected/error/permission-denied)"/>

        </map:match> <!-- End of "protected/selftest/document/type-name/*/id/*" matcher -->

        <!-- ============================================================================ -->
        <!-- h2: Context "training"                                                       -->
        <!-- ============================================================================ -->

        <map:match pattern="protected/training/document/type-name/*/id/*">
      
          <!-- Read permission check -->
          <map:act type="check-read-permission">
            <map:parameter name="type-name" value="{1}"/>
            <map:parameter name="id" value="{2}"/>
      
            <!-- ============== -->
            <!-- h3: Worksheets -->
            <!-- ============== -->

            <map:match pattern="protected/training/document/type-name/worksheet/id/*">
               <map:act type="provide-worksheet-infos">
                <map:parameter name="worksheet" value="{1}"/>
                <map:parameter name="time-format" value="dd.MM.yyyy HH:mm"/>
                <!-- This action provides the following sitemap parameters used below
                (it sets some more, but only these are used):
                  case
                      Distinguishes the following cases that may occur with this URL:
                      (1) user is staff member, 
                      (2) user is a course member and current time is before timeframe,
                      (3) user is a course member and current time is inside or after
                          timframe,
                      (4) user is not a course member
                  course
                      Id of the course
                  is-course-member
                      Whether the current user is a course member
                  worksheet
                      Id  of the worksheet
                  category
                      Category of the worksheet
                  label
                      Label of the worksheet
                  summary
                      Id of the generic summary
                  timeframe-start
                      Beginning of the timeframe
                  timeframe-start-raw
                      Beginning of the timeframe, as seconds since 00:00 Jan 01 1970
                  timeframe-end
                      End of the timeframe
                  timeframe-end-raw
                      End of the timeframe, as seconds since 00:00 Jan 01 1970
                -->



                <map:select type="simple">
                  <map:parameter name="value" value="{case}"/>

                  <!-- ======================== -->
                  <!-- h4: Case 1: Staff member -->
                  <!-- ======================== -->

                  <map:when test="staff">

                    <map:act type="setup-worksheet-for-staff">
                      <map:parameter name="student" value="{request-param:student}"/>
                      <map:parameter name="timeframe-relation" value="{timeframe-relation}"/>
                      <!-- This action sets the following sitemap parameters used below:
                        tutor-view
                            Whether tutor view is enabled
                        user
                            The id of the user whose problem data should be loaded
                        student-selection-failed
                            Whether the selection of a student failed
                        student-selection-error-message
                            Error message in case the selection of a student failed
                      -->

                      <map:act type="resolve-generic-document">
                        <map:parameter name="name-of-generic" value="generic_summary"/>
                        <map:parameter name="id-of-generic" value="{../summary}"/>
                        <map:parameter name="language" value="{session-attr:language}"/>
                        <map:parameter name="theme" value="{session-attr:theme}"/>

                        <map:generate type="document">
                          <map:parameter name="type-name" value="summary"/>
                          <map:parameter name="id" value="{id-of-real}"/>
                        </map:generate>

                        <map:transform type="add-params">
                          <map:parameter name="course" value="{../../course}"/>
                          <map:parameter name="worksheet" value="{../../worksheet}"/>
                          <map:parameter name="worksheet-category" value="{../../category}"/>
                          <map:parameter name="worksheet-label" value="{../../label}"/>
                          <map:parameter name="is-staff-member" value="yes"/>
                          <map:parameter name="timeframe-relation" value="{../../timeframe-relation}"/>
                          <map:parameter name="timeframe-start" value="{../../timeframe-start}"/>
                          <map:parameter name="timeframe-start-raw" value="{../../timeframe-start-raw}"/>
                          <map:parameter name="timeframe-end" value="{../../timeframe-end}"/>
                          <map:parameter name="timeframe-end-raw" value="{../../timeframe-end-raw}"/>
                          <map:parameter name="session-user" value="{session-attr:user}"/>
                          <map:parameter name="tutor-view" value="{../tutor-view}"/>
                          <map:parameter name="student-selection-failed" value="{../student-selection-failed}"/>
                          <map:parameter name="student-selection-error-message" value="{../student-selection-error-message}"/>
                          <map:parameter name="with-load-button" value="{request-param:with-load-button}"/>
                          <map:parameter name="as-frame" value="{request-param:as-frame}"/>
                        </map:transform>
                      
                        <map:transform type="add-user">
                          <map:parameter name="id" value="{../user}"/>
                        </map:transform>

                        <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme}, system/course/g_xsl_worksheet_summary.meta.xml, generic_xsl_stylesheet)"/>
                        <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/xsl_stylesheet/id/%{id}, system/common/xsl_math_signfix.meta.xml, xsl_stylesheet)"/>

                        <map:call resource="xhtml"/>

                      </map:act> <!-- End of resolve-generic-document action -->
                    </map:act> <!-- End of setup-worksheet-for-staff action -->

                  </map:when> <!-- End of case 1 -->

                  <!-- =========================================== -->
                  <!-- h4: Case 2: Course member; before timeframe -->
                  <!-- =========================================== -->

                  <map:when test="student-before-timeframe">
                    <map:redirect-to smac:uri="resolve-path(%{prefix}/protected/view/document/type-name/generic_page/id/%{id}, system/course/g_pge_worksheet_before_timeframe.meta.xml, generic_page)"/>
                  </map:when>

                  <!-- ==================================================== -->
                  <!-- h4: Case 3: Course member; inside or after timeframe -->
                  <!-- ==================================================== -->

                  <map:when test="student-not-before-timeframe">
                    <map:act type="resolve-generic-document">
                      <map:parameter name="name-of-generic" value="generic_summary"/>
                      <map:parameter name="id-of-generic" value="{summary}"/>
                      <map:parameter name="language" value="{session-attr:language}"/>
                      <map:parameter name="theme" value="{session-attr:theme}"/>

                      <map:generate type="document">
                        <map:parameter name="type-name" value="summary"/>
                        <map:parameter name="id" value="{id-of-real}"/>
                      </map:generate>

                      <map:transform type="add-params">
                        <map:parameter name="course" value="{../course}"/>
                        <map:parameter name="worksheet" value="{../worksheet}"/>
                        <map:parameter name="worksheet-category" value="{../category}"/>
                        <map:parameter name="worksheet-label" value="{../label}"/>
                        <map:parameter name="is-course-member" value="{../is-course-member}"/>
                        <map:parameter name="timeframe-relation" value="{../timeframe-relation}"/>
                        <map:parameter name="timeframe-start" value="{../timeframe-start}"/>
                        <map:parameter name="timeframe-start-raw" value="{../timeframe-start-raw}"/>
                        <map:parameter name="timeframe-end" value="{../timeframe-end}"/>
                        <map:parameter name="timeframe-end-raw" value="{../timeframe-end-raw}"/>
                        <map:parameter name="session-user" value="{session-attr:user}"/>
                        <map:parameter name="with-load-button" value="{request-param:with-open-button}"/>
                        <map:parameter name="as-frame" value="{request-param:as-frame}"/>
                      </map:transform>
                      
                      <map:transform type="add-user"/>

                      <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme}, system/course/g_xsl_worksheet_summary.meta.xml, generic_xsl_stylesheet)"/>
                      <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/xsl_stylesheet/id/%{id}, system/common/xsl_math_signfix.meta.xml, xsl_stylesheet)"/>

                      <map:call resource="xhtml"/>

                    </map:act> <!-- End of resolve-generic-document action -->

                  </map:when>  <!-- End of case 3 -->

                  <!-- =============================== -->
                  <!-- h4: Case 4: Not a course member -->
                  <!-- =============================== -->

                  <map:when test="other">
                    <map:redirect-to smac:uri="resolve-path(%{prefix}/protected/view/document/type-name/generic_page/id/%{id}, system/course/g_pge_worksheet_not_a_course_member.meta.xml, generic_page)"/>
                  </map:when>
                  
                </map:select>

              </map:act> <!-- End of provide-worksheet-infos action -->

            </map:match> <!-- End of "protected/training/document/type-name/worksheet/id/*" match -->

            <!-- ============ -->
            <!-- h3: Problems -->
            <!-- ============ -->
      
            <map:match pattern="protected/training/document/type-name/problem/id/*">
              <map:act type="provide-worksheet-infos">
                <map:parameter name="ref" value="{request-param:ref}"/>
                <!-- This action provides the following sitemap parameters used below
                (it sets some more, but only these are used):
                  case
                      Distinguishes the following cases that may occur with this URL:
                      (1) user is staff member, 
                      (2) user is a course member and current time is before timeframe,
                      (3) user is a course member and current time is inside or after
                          timframe,
                      (4) user is not a course member
                  course
                      Id of the course
                  worksheet
                      Id  of the worksheet
                  category
                      Category of the worksheet
                  is-course-member
                      Whether the user is a member of the course
                  timeframe-relation
                      Whether we are before, inside, or after the timeframe
                  after-timeframe
                      Whether we are after the timeframe
                -->

                  <map:select type="simple">
                    <map:parameter name="value" value="{case}"/>
        
                    <!-- ======================== -->
                    <!-- h4: Case 1: Staff member -->
                    <!-- ======================== -->
        
                    <map:when test="staff">
                      <map:act type="setup-training-problem">
                        <map:parameter name="ref" value="{request-param:ref}"/>
                        <map:parameter name="problem" value="{../1}"/>
                        <map:parameter name="clear-data" value="{request-param:clear-data}"/>
                        <map:parameter name="correct" value="{request-param:correct}"/>
                        <!-- This action sets the following sitemap parameters used below:
                          include-correction
                              Whether the correction should be added
                          corrected
                              Whether the action triggered a correction of the user answers
                        -->
        
                        <map:generate label="generator">
                          <map:parameter name="type-name" value="problem"/>
                          <map:parameter name="id" value="{../../1}"/>
                          <map:parameter name="from-doc-type-name" value="worksheet"/>
                        </map:generate>
        
                        <map:transform type="add-params">
                          <map:parameter name="course" value="{../course}"/>
                          <map:parameter name="worksheet" value="{../worksheet}"/>
                          <map:parameter name="worksheet-category" value="{../category}"/>
                          <map:parameter name="is-staff-member" value="yes"/>
                          <map:parameter name="corrected"
                                        value="{corrected}"/>
                          <map:parameter name="timeframe-relation"
                                        value="{../timeframe-relation}"/>
                          <map:parameter name="session-user" value="{session-attr:user}"/>
                          <map:parameter name="as-frame" value="{request-param:as-frame}"/>
                        </map:transform>
    
                        <map:transform type="add-user"/>
        
                        <map:transform type="add-problem-datasheet" label="xml">
                          <map:parameter name="ref" value="{request-param:ref}"/>
                          <map:parameter name="problem" value="{../../1}"/>
                          <map:parameter name="with-correction" value="{include-correction}"/>
                          <map:parameter name="with-common-data" value="{include-correction}"/>
                          <map:parameter name="time-format" value="dd.MM.yyyy HH:mm"/>
                        </map:transform>
        
                        <map:transform type="add-reference">
                          <map:parameter name="from-doc-type" smac:value="doctype-name-to-code(worksheet)"/>
                          <map:parameter name="to-doc-type" smac:value="doctype-name-to-code(generic_problem)"/>
                          <map:parameter name="id" value="{request-param:ref}"/>
                        </map:transform>
        
                        <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/problem/g_xsl_problem.meta.xml,generic_xsl_stylesheet)"/>
                        <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/xsl_stylesheet/id/%{id},system/common/xsl_math_signfix.meta.xml,xsl_stylesheet)"/>
        
                        <map:call resource="xhtml"/>
        
                      </map:act> <!-- End of action "setup-problem-for-staff" -->
                    </map:when> <!-- End of case 1 -->
    
        
                    <!-- =========================================== -->
                    <!-- h4: Case 2: Course member; before timeframe -->
                    <!-- =========================================== -->
        
                    <map:when test="student-before-timeframe">
                      <map:redirect-to smac:uri="resolve-path(%{prefix}/protected/view/document/type-name/generic_page/id/%{id}, system/problem/g_pge_before_timeframe.meta.xml, generic_page)"/>
                    </map:when>
        
                    <!-- ==================================================== -->
                    <!-- h4: Case 3: Course member; inside or after timeframe -->
                    <!-- ==================================================== -->
        
                    <map:when test="student-not-before-timeframe">
                      <map:act type="setup-training-problem">
                        <map:parameter name="ref" value="{request-param:ref}"/>
                        <map:parameter name="problem" value="{../1}"/>
                        <map:parameter name="clear-data" value="{request-param:clear-data}"/>
                        <map:parameter name="correct" value="{request-param:correct}"/>
                        <!-- This action sets the following sitemap parameters used below:
                          include-correction
                              Whether the correction should be added
                          corrected
                              Whether the action triggered a correction of the user answers
                        -->
        
                        <map:generate label="generator">
                          <map:parameter name="type-name" value="problem"/>
                          <map:parameter name="id" value="{../../1}"/>
                          <map:parameter name="from-doc-type" smac:value="doctype-name-to-code(worksheet)"/>
                        </map:generate>
        
                        <map:transform type="add-reference">
                          <map:parameter name="from-doc-type" smac:value="doctype-name-to-code(worksheet)"/>
                          <map:parameter name="to-doc-type" smac:value="doctype-name-to-code(generic_problem)"/>
                          <map:parameter name="id" value="{request-param:ref}"/>
                        </map:transform>
        
                        <map:transform type="add-params">
                          <map:parameter name="course" value="{../course}"/>
                          <map:parameter name="worksheet" value="{../worksheet}"/>
                          <map:parameter name="worksheet-category" value="{../category}"/>
                          <map:parameter name="is-course-member" value="{../is-course-member}"/>
                          <map:parameter name="corrected" value="{corrected}"/>
                          <map:parameter name="timeframe-relation" value="{../timeframe-relation}"/>
                          <map:parameter name="session-user" value="{session-attr:user}"/>
                          <map:parameter name="as-frame" value="{request-param:as-frame}"/>
                        </map:transform>
        
                        <map:transform type="add-user"/>
    
                        <map:transform type="add-problem-datasheet" label="xml">
                          <map:parameter name="ref" value="{request-param:ref}"/>
                          <map:parameter name="problem" value="{../../1}"/>
                          <map:parameter name="with-correction" value="{include-correction}"/>
                          <map:parameter name="with-common-data" value="{include-correction}"/>
                          <map:parameter name="time-format" value="dd.MM.yyyy HH:mm"/>
                        </map:transform>
        
                      <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/problem/g_xsl_problem.meta.xml,generic_xsl_stylesheet)"/>
                      <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/xsl_stylesheet/id/%{id},system/common/xsl_math_signfix.meta.xml,xsl_stylesheet)" label="xml"/>
        
                      <map:call resource="xhtml"/>      
                      </map:act> 
                    </map:when>
        
                    <!-- ================================ -->
                    <!-- h4: Case 4: Not a course member  -->
                    <!-- ================================ -->
        
                    <map:when test="other">
                      <map:redirect-to smac:uri="resolve-path(%{prefix}/protected/view/document/type-name/generic_page/id/%{id}, system/problem/g_pge_not_a_course_member.meta.xml, generic_page)"/>
                    </map:when>
                    
                  </map:select>
              </map:act> <!-- End of action "provide-worksheet-infos" -->
  
            </map:match> <!-- End of match "protected/training/document/type-name/problem/id/*" -->
           </map:act>
        </map:match>


        <!-- ============================================================================ -->
        <!-- h2: Context "nav"                                                            -->
        <!-- ============================================================================ -->

        <map:match pattern="protected/nav/document/type-name/*/id/*">

          <!-- Read permission check -->
          <map:act type="check-read-permission">
            <map:parameter name="type-name" value="{1}"/>
            <map:parameter name="id" value="{2}"/>

            <!-- The navigation net of a course -->
            <map:match pattern="protected/nav/document/type-name/course/id/*">
              <map:generate>
                <map:parameter name="type" smac:value="doctype-name-to-code(course)"/>
                <map:parameter name="id" value="{1}"/>
              </map:generate>
              <map:transform type="add-current-time">
                <map:parameter name="use-client-time" value="true"/>
              </map:transform>
              <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/course/g_xsl_course_nav.meta.xml,generic_xsl_stylesheet)"/>
              <map:call resource="xhtml"/>
            </map:match>
            
            <!-- The navigation net of a course_section -->
            <map:match pattern="protected/nav/document/type-name/course_section/id/*">
              <map:act type="provide-parent-course">
                <map:parameter name="child-type-name" value="course_section"/>
                <map:parameter name="child-id" value="{1}"/>
 
                <map:generate>
                  <map:parameter name="type" smac:value="doctype-name-to-code(course_section)"/>
                  <map:parameter name="id" value="{../1}"/>
                </map:generate>
                <map:transform type="add-params">
                  <map:parameter name="parent-id" value="{parent-id}"/>
                  <map:parameter name="class" value="{parent-class-id}"/>
                </map:transform>

                <map:transform type="resolve-generic-refs">
                  <map:parameter name="type" smac:value="doctype-name-to-code(course_section)"/>
                  <map:parameter name="id" value="{../1}"/>
                  <map:parameter name="language" value="{session-attr:language}"/>
                  <map:parameter name="theme" value="{session-attr:theme}"/>
                </map:transform>
                <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/course/g_xsl_course_nav.meta.xml,generic_xsl_stylesheet)"/>

                <map:call resource="xhtml"/>
             </map:act>  
            </map:match>
      
            <!-- The navigation net of a worksheet -->
            <map:match pattern="protected/nav/document/type-name/worksheet/id/*">

                <map:act type="provide-parent-course">
                  <map:parameter name="child-type-name" value="worksheet"/>
                  <map:parameter name="child-id" value="{1}"/>


                  <!-- User group check -->
                  <map:act type="check-user-groups"> 
                    <map:parameter name="user-groups"
                                  smac:value="ugr-names-to-ids(lecturers,tutors,admins)"/>
                    <!-- version for staff: -->
                    <map:generate>
                      <map:parameter name="type" smac:value="doctype-name-to-code(worksheet)"/>
                      <map:parameter name="id" value="{../../1}"/>
                    </map:generate>
                    <map:transform type="add-params">
                      <map:parameter name="parent-id" value="{../parent-id}"/>
                      <map:parameter name="tutor_view" value="yes"/>
                      <map:parameter name="class" value="{../parent-class-id}"/>
                    </map:transform>

                    <map:transform type="resolve-generic-refs">
                      <map:parameter name="type" smac:value="doctype-name-to-code(worksheet)"/>
                      <map:parameter name="id" value="{../../1}"/>
                      <map:parameter name="language" value="{session-attr:language}"/>
                      <map:parameter name="theme" value="{session-attr:theme}"/>
                    </map:transform>
                    <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/course/g_xsl_course_nav.meta.xml,generic_xsl_stylesheet)"/>
  
                    <map:call resource="xhtml"/>

                  </map:act> <!-- End of check-user-groups action -->
                  <!-- version for students: -->
                  <map:generate>
                    <map:parameter name="type" smac:value="doctype-name-to-code(worksheet)"/>
                    <map:parameter name="id" value="{../1}"/>
                  </map:generate>
                  <map:transform type="add-params">
                    <map:parameter name="parent-id" value="{parent-id}"/>
                    <map:parameter name="class" value="{parent-class-id}"/>
                  </map:transform>
             
                  <map:parameter name="class" value="{request-param:class}"/> 
                  <map:transform type="resolve-generic-refs">
                    <map:parameter name="type" smac:value="doctype-name-to-code(worksheet)"/>
                    <map:parameter name="id" value="{../1}"/>
                    <map:parameter name="language" value="{session-attr:language}"/>
                    <map:parameter name="theme" value="{session-attr:theme}"/>
                  </map:transform>
                  <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/course/g_xsl_course_nav.meta.xml,generic_xsl_stylesheet)"/>
  
                  <map:call resource="xhtml"/>
                </map:act> 
              </map:match>
  
            
          </map:act><!-- End of check-read-permission action -->

          <!-- Redirect to error page -->
          <map:redirect-to smac:uri="add-url-prefix(protected/error/permission-denied)"/>

        </map:match> <!-- End of "protected/nav/document/type-name/*/id/*" matcher -->

        <!-- ============================================================================ -->
        <!-- h2: Context "checkin"                                                        -->
        <!-- ============================================================================ -->

        <map:match pattern="protected/checkin/**">

          <!-- User group check -->
          <map:act type="check-user-groups">
            <map:parameter name="user-groups" smac:value="ugr-names-to-ids(lecturers,syncs,admins)"/>

            <!-- Does the actual checkin -->
            <map:match pattern="protected/checkin/checkin">
              <map:generate type="checkin"/>
              <map:transform src="fs_content/checkin_response.xsl"/>
              <map:serialize type="text"/>
            </map:match>

            <!-- Does the actual checkin -->
            <map:match pattern="protected/checkin/create-problem-wrapper">
              <map:generate type="create-problem-wrapper"/>
              <map:serialize type="xml"/>
            </map:match>

          </map:act> <!-- End of check-user-groups action -->

          <!-- Redirect to error page -->
          <map:redirect-to smac:uri="add-url-prefix(protected/error/permission-denied)"/>

        </map:match> <!-- End of "protected/checkin/**" matcher -->

        <!-- ============================================================================ -->
        <!-- h2: Context "alias"                                                          -->
        <!-- ============================================================================ -->

        <!-- Start page: -->
        <map:match pattern="protected/alias/start">
          <map:redirect-to
            smac:uri="resolve-path
                        (%{prefix}/protected/view/document/type-name/generic_page/id/%{id},
                        system/start/g_pge_start.meta.xml,
                        generic_page)"/>
        </map:match>

        <!-- Administrator's page: -->
        <map:match pattern="protected/alias/admin">
          <map:redirect-to
            smac:uri="resolve-path
                        (%{prefix}/protected/view/document/type-name/generic_page/id/%{id},
                        system/admin/g_pge_admin_home.meta.xml,
                        generic_page)"/>
        </map:match>

        <!-- Classes and Courses page: -->
        <map:match pattern="protected/alias/courses">
          <map:redirect-to
            smac:uri="add-url-prefix(protected/view/classes-and-courses-index)"/>
        </map:match>

        <map:match pattern="protected/alias/db-browser">
          <map:redirect-to smac:uri="add-url-prefix(protected/info/pseudo-document/type-name/section/id/0)"/>
        </map:match>

        <map:match pattern="protected/alias/mm-browser">
          <map:redirect-to smac:uri="add-url-prefix(protected/mmbrowser/pseudo-document/type-name/section/id/0)"/>
        </map:match>

        <map:match pattern="protected/alias/void">
          <map:redirect-to smac:uri="resolve-path
                        (%{prefix}/protected/view/document/type-name/generic_page/id/%{id},
                        system/misc/g_pge_void.meta.xml,
                        generic_page)"/>
        </map:match>

        <!-- Account overview page: -->
      <map:match pattern="protected/alias/account">
          <map:redirect-to smac:uri="resolve-path
                        (%{prefix}/protected/view/document/type-name/generic_page/id/%{id},
                        system/account/g_pge_account.meta.xml,
                        generic_page)"/>
        </map:match>

        <!-- ============================================================================ -->
        <!-- h2: Context "qf"                                                             -->
        <!-- ============================================================================ -->

        <!-- QF for applets: -->
        <map:match pattern="protected/qf/applet">
          <map:act type="qf-applet">
            <map:parameter name="subject" value="{request-param:subject}"/>
            <map:parameter name="report" value="{request-param:report}"/>
            <map:read type="string">
              <map:parameter name="string" value="{status}"/>
            </map:read>
          </map:act>
        </map:match>

        <!-- ============================================================================ -->
        <!-- h2: Context "sync"                                                           -->
        <!-- ============================================================================ -->

        <map:match pattern="protected/sync/*">

          <!-- User group check -->
          <map:act type="check-user-groups">
            <map:parameter name="user-groups" smac:value="ugr-names-to-ids(syncs,admins)"/>

            <map:act type="sync">
              <map:parameter name="command" value="{../1}"/>
              <map:read type="string">
                <map:parameter name="string" value="{status}"/>
              </map:read>
            </map:act>

          </map:act> <!-- End of check-user-groups action -->

          <!-- Redirect to error page -->
          <map:redirect-to smac:uri="add-url-prefix(protected/error/permission-denied)"/>

        </map:match> <!-- End of "protected/sync/*" matcher -->

        <!-- ============================================================================ -->
        <!-- h2: Context "correction"                                                           -->
        <!-- ============================================================================ -->

        <map:match pattern="protected/correction/worksheet/id/*">

          <map:act type="provide-worksheet-infos">
            <map:parameter name="worksheet" value="{1}"/>

            <map:select type="simple">
              <map:parameter name="value" value="{case}"/>

              <map:when test="staff">
                <map:generate type="worksheet-bulk-correction">
                  <map:parameter name="worksheet" value="{../1}"/>
                  <map:parameter name="force" value="{request-param:force}"/>
                </map:generate>
                <map:serialize type="xml"/>
              </map:when>

              <map:otherwise>
                <!-- Redirect to error page -->
                <map:redirect-to smac:uri="add-url-prefix(protected/error/permission-denied)"/>
              </map:otherwise>

            </map:select>
          </map:act> <!-- End of action "provide-worksheet-infos" -->

        </map:match> <!-- End of "protected/correction/worksheet/*" matcher -->

        <!-- ============================================================================ -->
        <!-- h2: Context "move"                                                           -->
        <!-- ============================================================================ -->

        <map:match pattern="protected/move/content-object">
          <map:act type="move-content-object">
            <map:parameter name="source" value="{request-param:source}"/>
            <map:parameter name="target" value="{request-param:target}"/>
            <map:read type="string">
              <map:parameter name="string" value="Ok"/>
            </map:read>
          </map:act>
        </map:match>

        <!-- ============================================================================ -->
        <!-- h2: Context "account"                                                          -->
        <!-- ============================================================================ -->

        <map:match pattern="protected/account/change-password">
          <map:redirect-to smac:uri="resolve-path                     (%{prefix}/protected/view/document/type-name/generic_page/id/%{id},
           system/account/g_pge_change_password.meta.xml,
           generic_page)"/>
        </map:match>

        <!--  Form to change language -->
        <map:match pattern="protected/account/change-language-request">

          <map:generate type="pseudo-document-index">
            <map:parameter name="type-name" value="language"/>
          </map:generate>
          <map:transform type="add-user"/>
          <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/account/g_xsl_change_language.meta.xml,generic_xsl_stylesheet)"/>
          <map:call resource="xhtml"/>
          
        </map:match>

        <!-- Execute change language -->
        <map:match pattern="protected/account/change-language">

          <map:act type="change-language">
            <map:parameter name="old-language" value="{session-attr:language}"/>
            <map:parameter name="new-language" value="{request-param:new-language}"/>

            <map:act type="resolve-generic-document">
              <map:parameter name="name-of-generic" value="generic_page"/>
              <map:parameter name="id-of-generic"
                             smac:value="resolve-path(
                                           %{id},
                                           system/account/g_pge_change_language.meta.xml,
                                           generic_page)"/>
              <map:parameter name="language" value="{session-attr:language}"/>
              <map:parameter name="theme" value="{session-attr:theme}"/>
            
              <map:generate type="document">
                <map:parameter name="type-name" value="page"/>
                <map:parameter name="id" value="{id-of-real}"/>
              </map:generate>

              <map:transform type="add-params">
                <map:parameter name="status" value="{../status}"/>
              </map:transform>
              <map:transform type="add-user"/>
              <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/misc/g_xsl_page.meta.xml,generic_xsl_stylesheet)" label="xml"/>
              <map:call resource="xhtml"/>
            </map:act>
          </map:act>
        </map:match>

        <!--  Form to change theme -->
        <map:match pattern="protected/account/change-theme-request">

          <map:generate type="pseudo-document-index">
            <map:parameter name="type-name" value="theme"/>
          </map:generate>
          <map:transform type="add-user"/>
          <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/account/g_xsl_change_theme.meta.xml,generic_xsl_stylesheet)"/>
          <map:call resource="xhtml"/>
          
        </map:match>

        <!-- Execute change theme -->
        <map:match pattern="protected/account/change-theme">

          <map:act type="change-theme">
            <map:parameter name="old-theme" value="{session-attr:theme}"/>
            <map:parameter name="new-theme" value="{request-param:new-theme}"/>

            <map:act type="resolve-generic-document">
              <map:parameter name="name-of-generic" value="generic_page"/>
              <map:parameter name="id-of-generic"
                             smac:value="resolve-path(
                                           %{id},
                                           system/account/g_pge_change_theme.meta.xml,
                                           generic_page)"/>
              <map:parameter name="language" value="{session-attr:language}"/>
              <map:parameter name="theme" value="{session-attr:theme}"/>
            
              <map:generate type="document">
                <map:parameter name="type-name" value="page"/>
                <map:parameter name="id" value="{id-of-real}"/>
              </map:generate>

              <map:transform type="add-params">
                <map:parameter name="status" value="{../status}"/>
              </map:transform>
              <map:transform type="add-user"/>
              <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/misc/g_xsl_page.meta.xml,generic_xsl_stylesheet)" label="xml"/>
              <map:call resource="xhtml"/>
            </map:act>
          </map:act>
        </map:match>

        <!-- ============================================================================ -->
        <!-- h2: Context "manage"                                                          -->
        <!-- ============================================================================ -->

        <map:match pattern="protected/manage/**">

          <map:act type="check-if-staff">
            <map:parameter name="class" value="{request-param:class}"/>

            <map:match pattern="protected/manage/change-tutorial">
              <map:act type="change-tutorial">
                <map:parameter name="form-stage" value="{request-param:form-stage}"/>
                <map:parameter name="user" value="{request-param:user}"/>
                <map:parameter name="class" value="{request-param:class}"/>
                <map:parameter name="old-tutorial" value="{request-param:old-tutorial}"/>
                <map:parameter name="new-tutorial" value="{request-param:new-tutorial}"/>

                <!-- The user; contains also a list of all tutorials the user is a member of -->
                <map:generate type="user">
                  <map:parameter name="type-name" value="user"/>
                  <map:parameter name="id" value="{request-param:user}"/>
                </map:generate>

                <!-- The class; contains also a list of all tutorials which belong to the class -->
                <map:transform type="add-pseudo-document">
                  <map:parameter name="type-name" value="class"/>
                  <map:parameter name="id" value="{request-param:class}"/>
                </map:transform>

                <map:transform type="add-params">
                  <map:parameter name="form-stage" value="{form-stage}"/>
                  <map:parameter name="old-tutorial" value="{request-param:old-tutorial}"/>
                  <map:parameter name="new-tutorial" value="{request-param:new-tutorial}"/>
                </map:transform>

                <map:transform type="add-user"/>

                <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/manage/g_xsl_change_tutorial.meta.xml,generic_xsl_stylesheet)"/>

                <map:call resource="xhtml"/>

              </map:act>
            </map:match>
          </map:act>

            <map:match pattern="protected/manage/edit-tutorial">
              <map:act type="check-if-staff">
                <map:parameter name="class" value="{request-param:class}"/>
                <map:parameter name="groups" value="admins,lecturers"/>

                <map:act type="edit-tutorial-unpriv">
  
                  <map:select type="simple">
                    <map:parameter name="value" value="{tutorial}"/>
  
                    <map:when test="-1">
                      <map:generate type="void-pseudo-document">
                        <map:parameter name="type-name" value="tutorial"/>
                      </map:generate>
                    </map:when>
  
                    <map:otherwise>
                      <map:generate type="pseudo-document">
                        <map:parameter name="type-name" value="tutorial"/>
                        <map:parameter name="id" value="{tutorial}"/>
                      </map:generate>
                    </map:otherwise>
  
                  </map:select>
  
                  <map:transform type="add-pseudo-document">
                    <map:parameter name="type-name" value="class"/>
                    <map:parameter name="id" value="{request-param:class}"/>
                    <map:parameter name="use-mode-name" value="component"/>
                  </map:transform>
  
                  <map:transform type="add-pseudo-document">
                    <map:parameter name="type-name" value="user_group"/>
                    <map:parameter name="id" smac:value="ugr-names-to-ids(tutors)"/>
                  </map:transform>
  
                  <map:transform type="add-params">
                    <map:parameter name="mode" value="{mode}"/>
                    <map:parameter name="performed-task" value="{performed-task}"/>
                  </map:transform>
  
                  <map:act type="check-if-non-empty">
                    <map:parameter name="test" value="{error}"/>
  
                    <map:transform type="add-params">
                      <map:parameter name="error" value="{../error}"/>
                    </map:transform>
  
                    <map:transform type="add-request-params">
                      <map:parameter name="exclude" value="form-stage"/>
                    </map:transform>
  
                  </map:act>
  
                  <map:transform type="add-user"/>
  
                  <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/manage/g_xsl_edit_tutorial_unpriv.meta.xml,generic_xsl_stylesheet)"/>
  
                  <map:call resource="xhtml"/>
                  
                  <!-- <map:serialize type="xml"/>  -->
                  
                </map:act>
              </map:act>
            </map:match>
                   
        </map:match>

        <!-- ============================================================================ -->
        <!-- h2: Context "admin"                                                          -->
        <!-- ============================================================================ -->

        <map:match pattern="protected/admin/**">

          <!-- User group check -->
          <map:act type="check-user-groups">
            <map:parameter name="user-groups" smac:value="ugr-names-to-ids(admins)"/>

            <!-- Admin home page -->
            <map:match pattern="protected/admin/home">
              <map:redirect-to smac:uri="resolve-path(%{prefix}/protected/view/document/type-name/generic_page/id/%{id},system/admin/g_pge_admin_home.meta.xml,generic_page)"/>
            </map:match>

            <!-- Clear cache -->
            <map:match pattern="protected/admin/clear-cache">
              <map:act type="clear-cache"/>
              <map:redirect-to              smac:uri="resolve-path(%{prefix}/protected/view/document/type-name/generic_page/id/%{id},system/admin/g_pge_clear_cache.meta.xml,generic_page)"/>
            </map:match>

            <!-- Clear persistent store -->
            <map:match pattern="protected/admin/clear-persistent-store">
              <map:act type="clear-persistent-store">
                <map:read type="string">
                  <map:parameter name="string" value="Persistent store cleared"/>
                </map:read>
              </map:act>
              <map:read type="string">
                <map:parameter name="string"
                               value="Failed to clear persistent store. See logs for details"/>
              </map:read>
            </map:match>

            <!-- Display the status of a service class as plain text -->
            <map:match pattern="protected/admin/status-plain/service/class-name/*">
              <map:read type="service-status">
                <map:parameter name="class-name" value="{1}"/>
                </map:read>
            </map:match>

            <!-- Display the status of a service class as raw XML -->
            <map:match pattern="protected/admin/status-raw/service/class-name/*">
              <map:generate type="service-status">
                <map:parameter name="class-name" value="{1}"/>
              </map:generate>
              <map:serialize type="xml"/>
            </map:match>

            <!-- Display the service status overview as raw XML -->
            <map:match pattern="protected/admin/status-raw/service/overview">
              <map:generate type="service-status-overview"/>
              <map:serialize type="xml"/>
            </map:match>

            <!-- Display the status of a service class as XHTML -->
            <map:match pattern="protected/admin/status/service/class-name/*">
              <map:generate type="service-status">
                <map:parameter name="class-name" value="{1}"/>
              </map:generate>
              <map:transform type="add-user"/>
              <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/admin/g_xsl_service_status.meta.xml,generic_xsl_stylesheet)"/> 
              <map:call resource="xhtml"/>
            </map:match>

            <!-- Display the service status overview as XHTML -->
            <map:match pattern="protected/admin/status/service/overview">
              <map:generate type="service-status-overview"/>
              <map:transform type="add-user"/>
              <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/admin/g_xsl_service_status_overview.meta.xml,generic_xsl_stylesheet)"/> 
              <map:call resource="xhtml"/>
            </map:match>

            <!-- Display active sessions -->
            <map:match pattern="protected/admin/status/sessions/overview">
              <map:generate type="session-list"/>
              <map:transform type="add-user"/>
              <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/admin/g_xsl_session_overview.meta.xml,generic_xsl_stylesheet)"/>
              <map:call resource="xhtml"/>
            </map:match>

            <!-- Display list of all users -->
            <map:match pattern="protected/admin/users">
              <map:generate type="pseudo-document-index">
                <map:parameter name="type-name" value="user"/>
                <map:parameter name="use-mode-name" value="info"/>
              </map:generate>
              <map:transform type="add-user"/>
              <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/admin/g_xsl_user_index.meta.xml,generic_xsl_stylesheet)"/>
              <map:call resource="xhtml"/>
            </map:match>

            <!-- Search users: -->
            <map:match pattern="protected/admin/search-users">
              <map:generate type="search-users"/>
              <map:transform type="add-user"/>
              <map:transform type="add-params">
                <map:parameter name="form-stage" value="{request-param:form-stage}"/>
              </map:transform>
              <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/admin/g_xsl_search_users.meta.xml,generic_xsl_stylesheet)"/>
              <map:call resource="xhtml"/>
            </map:match>

            <!-- Create or edit user-->
            <map:match pattern="protected/admin/edit-user">

              <map:act type="edit-user">

                <map:select type="simple">
                  <map:parameter name="value" value="{user}"/>

                  <map:when test="-1">
                    <map:generate type="void-user"/>
                  </map:when>

                  <map:otherwise>
                    <map:generate type="user">
                      <map:parameter name="id" value="{user}"/>
                    </map:generate>
                  </map:otherwise>

                </map:select>

                <map:transform type="add-params">
                  <map:parameter name="mode" value="{mode}"/>
                  <map:parameter name="performed-task" value="{performed-task}"/>
                </map:transform>

                <map:transform type="add-pseudodoc-index">
                  <map:parameter name="type-name" value="user_group"/>
                </map:transform>

                <map:transform type="add-pseudodoc-index">
                  <map:parameter name="type-name" value="class"/>
                </map:transform>

                <map:transform type="add-pseudodoc-index">
                  <map:parameter name="type-name" value="tutorial"/>
                </map:transform>

                <map:act type="check-if-non-empty">
                  <map:parameter name="test" value="{error}"/>

                  <map:transform type="add-params">
                    <map:parameter name="error" value="{../error}"/>
                  </map:transform>

                  <map:transform type="add-request-params">
                    <map:parameter name="exclude" value="form-stage"/>
                  </map:transform>

                </map:act>

                <map:transform type="add-user"/>

                <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/admin/g_xsl_edit_user.meta.xml,generic_xsl_stylesheet)"/>

                <map:call resource="xhtml"/>
                
                 <!-- <map:serialize type="xml"/>  -->

              </map:act>

            </map:match>

            <!-- Display db content -->
            <map:match pattern="protected/admin/db-content">

              <map:act type="resolve-generic-document">
                <map:parameter name="name-of-generic" value="generic_page"/>
                <map:parameter name="id-of-generic"
                               smac:value="resolve-path(
                                           %{id},
                                           system/admin/g_pge_db_content.meta.xml,
                                           generic_page)"/>
                <map:parameter name="language" value="{session-attr:language}"/>
                <map:parameter name="theme" value="{session-attr:theme}"/>
              
                <map:generate type="document">
                  <map:parameter name="type-name" value="page"/>
                  <map:parameter name="id" value="{id-of-real}"/>
                </map:generate>

                <map:transform type="add-user"/>
                <map:transform type="xslt" smac:src="resolve-path(cocoon:/protected/view/document/type-name/generic_xsl_stylesheet/id/%{id}/cache-hint/{session-attr:language}/{session-attr:theme},system/misc/g_xsl_page.meta.xml,generic_xsl_stylesheet)" label="xml"/>
                <map:call resource="xhtml"/>
              </map:act>

            </map:match>

            <!-- Sitemap creation -->
            <map:match pattern="protected/admin/create-sitemap">
              <map:generate type="upload"/>
              <map:transform type="sitemap"/>
              <map:serialize type="xml"/>
            </map:match>

          </map:act> <!-- End of check-user-groups action -->

          <!-- Redirect to error page -->
          <map:redirect-to smac:uri="add-url-prefix(protected/error/permission-denied)"/>

        </map:match> <!-- End of "protected/admin/*" matcher -->

      </map:act> <!-- End of protect action -->
      <!-- Redirect to error page -->
      <map:redirect-to smac:uri="add-url-prefix(protected/error/permission-denied)"/>
        
    </map:match> <!-- End of "protected/**" matcher -->

    <!-- Error handling -->
    <map:handle-errors>
      <map:read type="error"/>
    </map:handle-errors>

  </map:pipeline>

</map:pipelines>

</map:sitemap>
