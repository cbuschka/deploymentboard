import React from 'react';
import './Matrix.css';
import {Table} from "reactstrap";
import {Issue} from "./Issue";

export class Matrix extends React.Component {

    render() {
        const {systems = []} = this.props;

        const colWidth = Math.floor(95.0 / systems.length);

        return (
            <Table className="Matrix">
                <thead>
                <tr className="Matrix_topLine">
                    <th className="Matrix_cell Matrix_leftCol">&nbsp;</th>
                    {systems.map(system => {
                        return <th className="Matrix_cell " style={{"width": colWidth + "%"}}
                                   key={system.name}>{system.name}</th>
                    })}
                </tr>
                </thead>
                <tbody>
                <tr className="Matrix_environment">
                    <td className="Matrix_cell Matrix_leftCol">Issues</td>
                    {systems.map(system => {
                        const systemIssues = system.issues || [];

                        return <td className="Matrix_cell" style={{"width": colWidth + "%"}} key={system.name}>
                            <div className="Matrix_cellFill">
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
