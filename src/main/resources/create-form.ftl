<div class='entity' data-entity-id='quiz'>
    <form id="quiz-create-form|${entity['quiz'].quizId}">
        <div style='display:flex;padding-top:8px'>
            <div><img src="https://symphony.com/wp-content/uploads/2019/08/favicon.png" style='height:20px' /></div>
            <div style='padding-top:1px;padding-left:5px;'>
                <#if entity['quiz'].quizId?has_content>
                    <b>Create Next Question</b>
                <#else>
                    <b>Create New Quiz</b>
                </#if>
            </div>
        </div>

        <div style='height:2px;background:#0098ff;margin-top:10px;margin-bottom:10px'> </div>

        <h6>Question</h6>
        <textarea name="question" placeholder="Enter your quiz question.." required="true"></textarea>

        <h6>Answers</h6>
        <#list 1..entity["quiz"].count as option>
            <div style='margin-bottom:-10px;display:flex'>
                <#if option < 3 >
                    <#assign required="true">
                <#else>
                    <#assign required="false">
                </#if>
                <#if option == 1 >
                    <#assign checked="true">
                <#else>
                    <#assign checked="false">
                </#if>
                <div style="margin-top:10px;">
                    <radio name="answer" value="option${option}" checked="${checked}"></radio>
                </div>
                <div style="width:100%">
                    <text-field name="option${option}" placeholder="Option ${option}" required="${required}" />
                </div>
            </div>
        </#list>
        <br />

        <h6>Room Stream ID</h6>
        <#if entity['quiz'].quizId?has_content>
            <i>${entity['quiz'].targetStreamId}</i><br />
        <#else>
            <text-field name="targetStreamId" placeholder="Enter room stream id.." required="true"
                >${entity['quiz'].targetStreamId}</text-field>
        </#if>

        <h6>Time Limit</h6>
        <div style='display:flex'>
            <#list entity["quiz"].timeLimits as timeLimit>
                <#if timeLimit?index==0>
                    <#assign checked="true">
                <#else>
                    <#assign checked="false">
                </#if>
                <#if timeLimit==0>
                    <#assign label="None">
                <#else>
                    <#assign label="${timeLimit} secs">
                </#if>
                <div style='padding-right:6px;'><radio name="timeLimit" checked="${checked}" value="${timeLimit}">${label}</radio></div>
            </#list>
        </div>

        <div style='height:1px;background:#0098ff;margin-top:10px;margin-bottom:10px'> </div>
        <button name="nextQuestion" type="action">Next Question</button>
        <button name="launchQuiz" type="action">Launch Quiz</button>
    </form>
</div>
