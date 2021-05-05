import React from 'react';
import './Matrix.css';
import {Issue} from "./Issue";
import {StatusBadge} from "./StatusBadge";
import {InfoBlock} from "./InfoBlock";
import './DeploymentInfo.css';

export class DeploymentInfo extends React.Component {

    render() {
        return <div className="Matrix_system_environment">{this.renderContent()}</div>;
    }

    renderContent() {
        const {systemEnv} = this.props;

        if (!systemEnv) {
            return <div className="Matrix_system_environment_warning">n/a</div>;
        }

        const {
            ok,
            status,
            message,
            issues = [],
            version = "n/a",
            branch = "n/a",
            commitish = "n/a",
            buildTimestamp
        } = systemEnv;

        if (!ok) {
            return (<>
                <div className="Matrix_system_environment_toolLine">&nbsp;</div>
                <div className="Matrix_system_environment_content">
                    <span className="Matrix_system_environment_headline"><StatusBadge status={status}/></span>
                    <div className="Matrix_system_environment_warning">{message || "n/a"}</div>
                </div>
            </>);
        }

        return (<>
            <div className="Matrix_system_environment_toolLine">&nbsp;</div>
            <div className="Matrix_system_environment_content">
                <span className="Matrix_system_environment_headline"><StatusBadge
                    status={status}/>&nbsp;{version}</span>
                <div className="Matrix_system_environment_details">
                    {!!branch ? <InfoBlock text={`Branch: ${branch}`}/> : null}
                    {!!commitish ? <InfoBlock text={`Commit: ${commitish}`}/> : null}
                    {!!buildTimestamp ? <InfoBlock text={`Built at: ${buildTimestamp}`}/> : null}
                </div>

                {issues.map(issue => {
                    return <Issue key={issue.issueNo} issue={issue} className="Matrix_system_environment_issue"/>;
                })}
            </div>
        </>);
    }
}
