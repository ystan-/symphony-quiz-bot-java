<div class='entity' data-entity-id='quiz'>
    <form id="quiz-blast-form-${entity["quiz"].quizId},${entity["quiz"].questionIndex}">
        <div style='display:flex;padding-top:8px'>
            <div><img src="https://symphony.com/wp-content/uploads/2019/08/favicon.png" style='height:20px' /></div>
            <div style='padding-top:1px;padding-left:5px;'>
                <b>Quiz (${entity["quiz"].label}): ${entity["quiz"].question}</b>
            </div>
        </div>

        <div style='height:2px;background:#0098ff;margin-top:10px;margin-bottom:10px'> </div>

        <#list entity["quiz"].answers as answer>
            <button name="option-${answer?index}" type="action">${answer}</button>
        </#list>

        <div style='height:1px;background:#0098ff;margin-top:10px;margin-bottom:10px'> </div>

        <i>This question
            <#if entity["quiz"].timeLimit == 0>
                does not have a time limit
            <#else>
                will end in ${entity["quiz"].timeLimit} seconds
            </#if>
        </i>
    </form>
</div>
