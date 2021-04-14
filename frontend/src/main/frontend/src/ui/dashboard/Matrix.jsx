import React from 'react';
import './Matrix.css';
import classnames from 'classnames';

const InfoBlock = ({text}) => {
    if (!text) {
        return null;
    }

    return <span className="Matrix_InfoBlock">{text}</span>;
};

class DeploymentInfo extends React.Component {

    render() {
        return <td className="Matrix_system_environment">{this.renderContent()}</td>;
    }

    renderContent() {
        const {systemEnv} = this.props;

        if (!systemEnv) {
            return <div className="Matrix_system_environment_warning">n/a</div>;
        }

        const {
            ok,
            message,
            issues = [],
            version = "n/a",
            branch = "n/a",
            commitish = "n/a",
            buildTimestamp
        } = systemEnv;
        if (!ok) {
            return <div className="Matrix_system_environment_warning">{message || "n/a"}</div>
        }

        return <><span className="Matrix_system_environment_version">{version}</span>
            {!!branch ? <InfoBlock text={`Branch: ${branch}`}/> : null}
            {!!commitish ? <InfoBlock text={`Commit: ${commitish}`}/> : null}
            {!!buildTimestamp ? <InfoBlock text={`Built at: ${buildTimestamp}`}/> : null}

            {issues.map(issue => {
                return <span key={issue.issueNo}
                             className={classnames("Matrix_system_environment_issue", issue.status)}>{issue.issueNo}</span>;
            })}</>;
    }
}

export class Matrix
    extends React
        .Component {

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

                            return <DeploymentInfo key={systemName + "_" + envName} systemEnv={systemEnv}/>
                        })}
                    </tr>
                })}
                </tbody>
            </table>
        );
    }
}
