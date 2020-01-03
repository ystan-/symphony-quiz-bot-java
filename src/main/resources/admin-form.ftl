<div class='entity' data-entity-id='quiz'>
    The ${entity['quiz'].questionLabel} question has been published.
    You can use the buttons below
    <#if entity['quiz'].timeLimit gt 0>or wait ${entity['quiz'].timeLimit} seconds</#if>
    to advance.
    <br/><br/>

    <form id="admin-form-end-question">
        <#if entity['quiz'].finalQuestion>
            <#assign nextButtonLabel="Quiz">
        <#else>
            <#assign nextButtonLabel="Question">
        </#if>
        <button type="action" name="endQuestion">End ${nextButtonLabel}</button>
    </form>

    <#if !entity['quiz'].finalQuestion>
        <form id="admin-form-leaderboard">
            <button type="action" name="leaderboard">Show Leaderboard</button>
        </form>
        <form id="admin-form-next-question">
            <button type="action" name="nextQuestion">Next Question</button>
        </form>
    </#if>
</div>
