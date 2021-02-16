<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<c:set var="score" value="${ it.uuMax == 0 ? 0 : it.uu / it.uuMax * 100 }"/>
<c:set var="strength" value="${ it.strengthMax == 0 ? 0 : it.strength / it.strengthMax * 100 }"/>
<c:set var="knowhow" value="${ it.knowhowMax == 0 ? 0 : it.knowhow / it.knowhowMax * 100 }"/>
<c:set var="fitness" value="${ it.endurance / 30 * 100 }"/>
<c:set var="coverage"
       value="${ score <= 15 ? 'not' : score <= 50 ? 'partially' : score <= 85 ?  'largely' : 'fully'}"/>

<t:layout>
    <jsp:attribute name="scripts">
        <script src="js/dashboard.js" type="application/javascript"></script>
    </jsp:attribute>
    <jsp:attribute name="header">
        <jsp:include page="/WEB-INF/templates/header/cysec.jsp" />
    </jsp:attribute>
    <jsp:body>

        <div class="row">
            <div class="col-xs-7">
                <main>
                    <div class="row">
                        <div class="col-xs-6">
                            <h2>Congratulations, nice job!!</h2>
                        </div>
                        <div class="col-xs-6 text-right">
                            <h3>${score}%</h3>
                            <div>${coverage} implemented</div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-xs-12">
                            <p>You have ${coverage} achieved the company topics covered by
                                this questionnaire. You have implemented ${score}% of the recommended good
                                practices. Go back to the explanations, videos, and links provided with each
                                question to find out how to even further improve.</p>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-xs-12">
                            <!-- strength -->
                            <div class="row">
                                <div class="col-xs-1">
                                    <p class="skilllevel skill-strength"></p>
                                </div>
                                <div class="col-xs-2">
                                    <h3>Strength</h3>
                                    <p>Lvl. 1</p>
                                </div>
                                <div class="col-xs-8">
                                    <div class="progress">
                                        <div class="progress-bar" role="progressbar"
                                             style="width: ${strength}%;"
                                             aria-valuenow="${strength}" aria-valuemin="0" aria-valuemax="100">
                                        </div>
                                    </div>
                                </div>
                                <div class="col-xs-1">
    <%--                                +${strength}--%>
                                </div>
                            </div>
                            <!-- know how -->
                            <div class="row">
                                <div class="col-xs-1">
                                    <p class="skilllevel skill-knowhow"></p>
                                </div>
                                <div class="col-xs-2">
                                    <h3>Know-how</h3>
                                    <p>Lvl. 1</p>
                                </div>
                                <div class="col-xs-8">
                                    <div class="progress">
                                        <div class="progress-bar" role="progressbar"
                                             style="width: ${knowhow}%;"
                                             aria-valuenow="${knowhow}" aria-valuemin="0" aria-valuemax="100">
                                        </div>
                                    </div>
                                </div>
                                <div class="col-xs-1">
    <%--                                +${knowhow}--%>
                                </div>
                            </div>
                            <!-- fitness -->
                            <div class="row">
                                <div class="col-xs-1">
                                    <p class="skilllevel skill-fitness"></p>
                                </div>
                                <div class="col-xs-2">
                                    <h3>Fitness</h3>
                                    <p>Lvl. 1</p>
                                </div>
                                <div class="col-xs-8">
                                    <div class="progress">
                                        <div class="progress-bar" role="progressbar"
                                             style="width: ${fitness}%;"
                                             aria-valuenow="${fitness}" aria-valuemin="0" aria-valuemax="100"></div>
                                    </div>
                                </div>
                                <div class="col-xs-1">
    <%--                                +${fitness}--%>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-xs-8">
                            <p>Going back to the overview, you will see
                                recommended next steps</p>
                        </div>
                        <div class="col-xs-4">
                            <a href="${pageContext.request.contextPath}/app">
                                Back to overview
                            </a>
                        </div>
                    </div>
                </main>
            </div>
            <div class="col-xs-5" style="padding-right: 0;">
                <img class="img-responsive" style="max-height: calc(100vh - 84px); float: right;" alt="Responsive image"
                     src="${pageContext.request.contextPath}/api/rest/resources/lib-company/eu.smesec.library.company.CompanyLib/assets/images/lib-company-sidebar.jpg">
            </div>
        </div>
    </jsp:body>
</t:layout>