<div class='entity' data-entity-id='quiz'>
    <div style='display:flex;padding-top:8px'>
        <div><img src="https://symphony.com/wp-content/uploads/2019/08/favicon.png" style='height:20px' /></div>
        <div style='padding-top:1px;padding-left:5px;'>
            <b>${entity["quiz"].label}</b>
        </div>
    </div>

    <div style='height:2px;background:#0098ff;margin-top:10px;margin-bottom:10px'> </div>

    <table>
        <tr>
            <th>Rank</th>
            <th>Participant</th>
            <th style="text-align:right">Points</th>
            <th></th>
        </tr>
        <#list entity["quiz"].results as result>
            <tr>
                <td>${result?index+1}</td>
                <td>${result.displayName}</td>
                <td style="text-align:right">${result.count}</td>
                <td><div style='background-color:#0098ff;width:${result.width}px'> </div></td>
            </tr>
        </#list>
    </table>
</div>
