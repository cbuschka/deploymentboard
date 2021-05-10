import React from 'react';
import './IssuesMatrix.css';
import {Table} from "reactstrap";
import {Issue} from "../lib/Issue";

export class IssuesMatrix extends React.Component {

    render() {
        const {systems = []} = this.props;

        const colWidth = Math.floor(95.0 / systems.length);

        return (
            <Table className="IssuesMatrix">
                <thead>
                <tr className="IssuesMatrix_topLine">
                    <th className="IssuesMatrix_cell IssuesMatrix_leftCol">&nbsp;</th>
                    {systems.map(system => {
                        return <th className="IssuesMatrix_cell " style={{"width": colWidth + "%"}}
                                   key={system.name}>{system.name}</th>
                    })}
                </tr>
                </thead>
                <tbody>
                <tr className="IssuesMatrix_environment">
                    <td className="IssuesMatrix_cell IssuesMatrix_leftCol">Issues</td>
                    {systems.map(system => {
                        const systemIssues = system.issues || [];

                        return <td className="IssuesMatrix_cell" style={{"width": colWidth + "%"}} key={system.name}>
                            <div className="IssuesMatrix_cellFill">
                                {systemIssues.map(issue => {
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
