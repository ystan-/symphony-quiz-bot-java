<form id="quiz-create-form">
    <div class='entity' data-entity-id='quiz'>
        <div style='display:flex;padding-top:8px'>
            <div><img src="https://symphony.com/wp-content/uploads/2019/08/favicon.png" style='height:20px' /></div>
            <div style='padding-top:1px;padding-left:5px;'><b>Create New Quiz</b></div>
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

        <#if entity["quiz"].showPersonSelector>
            <h6>Audience</h6>
            <person-selector name="audience" placeholder="Select audience.." required="true" />
        </#if>

        <#if entity["quiz"].targetStreamId??>
            <h6>Room Stream ID</h6>
            <text-field name="targetStreamId" placeholder="Enter room stream id.."
                required="true">${entity['quiz'].targetStreamId}</text-field>
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
                <#elseif timeLimit==1>
                    <#assign label="1 minute">
                <#else>
                    <#assign label="${timeLimit} minutes">
                </#if>
                <div style='padding-right:6px;'><radio name="timeLimit" checked="${checked}" value="${timeLimit}">${label}</radio></div>
            </#list>
        </div>

        <div style='height:1px;background:#0098ff;margin-top:10px;margin-bottom:10px'> </div>
        <button name="createQuiz" type="action">Create Quiz</button>
    </div>
</form>
