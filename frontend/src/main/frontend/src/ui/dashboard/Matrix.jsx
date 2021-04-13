import React from 'react';
import './Matrix.css';
import classnames from 'classnames';

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
            <table className="Matrix">
                <thead>
                <tr>
                    <th className="Matrix_environment" style={{"width": "5%"}}>&nbsp;</th>
                    {systemNames.map(systemName => {
                        return <th className="Matrix_system" style={{"width": colWidth + "%"}}
                                   key={systemName}>{systemName}</th>
                    })}
                </tr>
                </thead>
                <tbody>
                {envNames.map(envName => {
                    const env = environments[envName];

                    return <tr className="Matrix_environment" key={envName}>
                        <td>{envName}</td>
                        {systemNames.map(systemName => {
                            const systemEnv = env[systemName];

                            return <td className="Matrix_system_environment"
                                       key={systemName + "_" + envName}><span
                                className="Matrix_system_environment_version">{systemEnv.version}</span>
                                <span
                                    className="Matrix_system_environment_branchAndCommitish">{systemEnv.branch} - {systemEnv.commitish}</span>
                                {systemEnv.issues.map(issue => {
                                    return <span key={issue.issueNo}
                                                 className={classnames("Matrix_system_environment_issue", issue.status)}>{issue.issueNo}</span>;
                                })}</td>
                        })}
                    </tr>
                })}
                </tbody>
            </table>
        );
    }
}
