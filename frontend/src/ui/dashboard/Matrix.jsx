import React from 'react';
import './Matrix.css';
import {Table} from "reactstrap";
import {DeploymentInfo} from "./DeploymentInfo";

export class Matrix extends React.Component {

    render() {
        const {environments} = this.props;

        const systemNames = [];
        const envNames = Object.keys(environments);
        Object.keys(environments).forEach(envName => {
            Object.keys(environments[envName]).forEach(systemName => {
                if (systemNames.indexOf(systemName) === -1) {
                    systemNames.push(systemName);
                }
            })
        })

        const colWidth = Math.floor(95.0 / systemNames.length);

        return (
            <Table className="Matrix">
                <thead>
                <tr className="Matrix_topLine">
                    <th className="Matrix_cell Matrix_leftCol">&nbsp;</th>
                    {systemNames.map(systemName => {
                        return <th className="Matrix_cell " style={{"width": colWidth + "%"}}
                                   key={systemName}>{systemName}</th>
                    })}
                </tr>
                </thead>
                <tbody>
                {envNames.map(envName => {
                    const env = environments[envName];

                    return <tr className="Matrix_environment" key={envName}>
                        <td className="Matrix_cell Matrix_leftCol">{envName}</td>
                        {systemNames.map(systemName => {
                            const systemEnv = env[systemName];

                            return <td className="Matrix_cell" style={{"width": colWidth + "%"}} key={systemName}>
                                <div className="Matrix_cellFill"><DeploymentInfo
                                    key={systemName + "_" + envName}
                                    systemEnv={systemEnv}/></div>
                            </td>
                        })}
                    </tr>
                })}
                </tbody>
            </Table>
        );
    }
}
