import React from 'react';
import './Issue.css';
import classnames from 'classnames';

export const Issue = ({issue, className}) => {
    const {issueNo, status, title} = issue;
    return <span className={classnames("Issue", className, status)}>
        <span className="Issue__title_issueNo">{issueNo}</span>
        <span className="Issue__title_issueStatus">&nbsp;({status})</span>
        {!!title ? <span className="Issue__title_issueSummary">{title}</span> : null}</span>;
};
