<form id="quiz-blast-form-${entity["quiz"].id}">
    <div class='entity' data-entity-id='quiz'>
        <div style='display:flex;padding-top:8px'>
            <div><img src="https://symphony.com/wp-content/uploads/2019/08/favicon.png" style='height:20px' /></div>
            <div style='padding-top:1px;padding-left:5px;'>
                <b>Quiz: ${entity["quiz"].question}</b> by <mention uid="${entity["quiz"].creatorId}" />
            </div>
        </div>

        <div style='height:2px;background:#0098ff;margin-top:10px;margin-bottom:10px'> </div>

        <#list entity["quiz"].answers as answer>
            <button name="option-${answer?index}" type="action">${answer}</button>
        </#list>

        <div style='height:1px;background:#0098ff;margin-top:10px;margin-bottom:10px'> </div>

        <i>This quiz
            <#if entity["quiz"].timeLimit == 0>
                does not have a time limit
            <#else>
                will end in ${entity["quiz"].timeLimit} minute<#if entity["quiz"].timeLimit gt 1>s</#if>
            </#if>
        </i>
    </div>
</form>
