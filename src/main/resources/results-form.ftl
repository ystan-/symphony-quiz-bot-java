<div class='entity' data-entity-id='quiz'>
    <div style='display:flex;padding-top:8px'>
        <div><img src="https://symphony.com/wp-content/uploads/2019/08/favicon.png" style='height:20px' /></div>
        <div style='padding-top:1px;padding-left:5px;'>
            <b>Question ${entity["quiz"].label}  Results for ${entity["quiz"].question}</b>
        </div>
    </div>

    <div style='height:2px;background:#0098ff;margin-top:10px;margin-bottom:10px'> </div>

    <table>
        <tr>
            <th>Answer</th>
            <th style="text-align:right">Votes</th>
            <th></th>
        </tr>
        <#list entity["quiz"].results as result>
            <#if result.correct>
                <#assign color="#28a428">
            <#else>
                <#assign color="#999999">
            </#if>
            <tr>
                <td style="color:${color}">${result.answer}</td>
                <td style="text-align:right;color:${color}">${result.count}</td>
                <td><div style='background-color:${color};width:${result.width}px'> </div></td>
            </tr>
        </#list>
    </table>
</div>
