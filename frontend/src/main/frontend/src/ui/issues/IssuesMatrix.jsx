import React from 'react';
import './IssuesMatrix.css';
import {Table} from "reactstrap";
import {Issue} from "../lib/Issue";

export class IssuesMatrix extends React.Component {

    render() {
        const {issueStreams = []} = this.props;

        const colWidth = Math.floor(99.0 / issueStreams.length);

        return (
            <Table className="IssuesMatrix">
                <thead>
                <tr className="IssuesMatrix_topLine">
                    {issueStreams.map(issueStream => {
                        return <th className="IssuesMatrix_cell" style={{"width": colWidth + "%"}}
                                   key={issueStream.system}>{issueStream.system}</th>
                    })}
                </tr>
                </thead>
                <tbody>
                <tr className="IssuesMatrix_environment">
                    {issueStreams.map(issueStream => {
                        const issues = issueStream.issues || [];

                        return <td className="IssuesMatrix_cell" style={{"width": colWidth + "%"}}
                                   key={issueStream.system}>
                            <div className="IssuesMatrix_cellFill IssuesMatrix_issues">
                                <div className="IssuesMatrix_issueStream_branch">{issueStream.branch}</div>
                                {issues.map(issue => {
                                    return <Issue key={issue.issueNo} issue={issue}/>;
                                })}
                            </div>
                        </td>
                    })}
                </tr>
                </tbody>
            </Table>
        );
    }
}
