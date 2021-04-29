import React from 'react';
import './Issue.css';
import classnames from 'classnames';

export class Issue extends React.Component {

    render() {
        const {issue: {issueNo, status, title, link}, className} = this.props;
        return <span className={classnames("Issue", className, status, !!link ? "Issue__linked" : null)}
                     onClick={this._onClick}>
        <span className="Issue__title_issueNo">{issueNo}</span>
        <span className="Issue__title_issueStatus">&nbsp;({status})</span>
            {!!title ? <span className="Issue__title_issueSummary">{title}</span> : null}</span>;
    }

    _onClick = () => {
        const {issue: {link}} = this.props;
        window.open(link, '_blank').focus();
    }
}
