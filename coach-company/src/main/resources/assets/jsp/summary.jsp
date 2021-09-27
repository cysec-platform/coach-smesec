<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>SMESEC Questionnaire Client</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap-3.3.7-dist/css/bootstrap.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/app/font-awesome-4.7.0/css/font-awesome.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/fonts.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/colors.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/typography.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/simple-sidebar.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css"/>

    <link id="contextPathHolder" data-contextPath="${pageContext.request.contextPath}"/>
    <script
            src="https://code.jquery.com/jquery-3.3.1.min.js"
            integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8="
            crossorigin="anonymous"></script>
    <script src=https://cdn.jsdelivr.net/npm/promise-polyfill@8.1/dist/polyfill.min.js></script>
    <script src=https://cdn.jsdelivr.net/npm/whatwg-fetch@3.0/dist/fetch.umd.min.js></script>

    <!-- this version of jquery is needed for accordion -->
    <script src="${pageContext.request.contextPath}/js/jquery-ui-1.12.1/jquery-ui.min.js"
            type="application/javascript"></script>

    <script src="${pageContext.request.contextPath}/js/questionnaireController.js.jsp"
            type="application/javascript"></script>

    <script>
        $(function () {
            $("#accordion").accordion({collapsible: true, active: false, heightStyle: 'content'});
        });
    </script>
</head>
<body>
<header class="questionnaire-nav bg-transparent">
    <div class="row">
        <div class="col-xs-7 bg-white">
            <div class="questionnaire-nav-logo">
                <a href="${pageContext.request.contextPath}/app"><img
                        src="${pageContext.request.contextPath}/assets/logo/CYSEC_Logo_RGB.svg"
                        width="106px" height="44px"/></a>
            </div>
        </div>
        <div class="col-xs-5 text-right">
            <div class="questionnaire-nav-close"><a href="${pageContext.request.contextPath}/app"><img
                    src="${pageContext.request.contextPath}/assets/icons/icn_close.svg" width="24px" height="24px"/></a>
            </div>
        </div>
    </div>
</header>

<c:set var="score" value="${ it.strength * 100 }"/>
<c:set var="strength" value="${ it.strength * 100 }"/>
<c:set var="knowhow" value="${ it.knowhow * 100 }"/>
<c:set var="fitness" value="${ it.endurance * 100 }"/>
<div class="container-fluid">
    <div class="row">
        <div class="col-xs-7">
            <main>
                <c:set var="coverage"
                       value="${ score <= 15 ? 'not' : score <= 50 ? 'partially' : score <= 85 ?  'largely' : 'fully'}"/>
                <div class="row">
                    <div class="col-xs-6">
                        <h2>Congratulations, nice job!</h2>
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
                 src="${pageContext.request.contextPath}/api/rest/resources/lib-company/eu.smesec.cysec.coach.company.CompanyLib/assets/images/lib-company-sidebar.jpg">
        </div>
    </div>
</div>

<script>
    $("#menu-toggle").click(function (e) {
        e.preventDefault();
        $("#wrapper").toggleClass("toggled");
    });
</script>


</body>
</html>
