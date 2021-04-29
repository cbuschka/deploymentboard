import React from 'react';
import './Matrix.css';
import classnames from 'classnames';
import {Table} from "reactstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faLock, faLockOpen} from '@fortawesome/free-solid-svg-icons'
import {Issue} from "./Issue";

const InfoBlock = ({text}) => {
    if (!text) {
        return null;
    }

    return <span className="Matrix_InfoBlock">{text}</span>;
};

const LockStatus = ({value, disabled = true}) => {
    if (!value) {
        return null;
    }

    switch (value) {
        case "UNLOCKED":
            return <div className={classnames("LockStatus", value, disabled ? "disabled" : "")}><FontAwesomeIcon
                icon={faLockOpen}/></div>;
        case "LOCKED":
            return <div className={classnames("LockStatus", value, disabled ? "disabled" : "")}><FontAwesomeIcon
                icon={faLock}/></div>;
        case "NOT_LOCKABLE":
            return <div className={classnames("LockStatus", value, "disabled")}><FontAwesomeIcon
                icon={faLock}/></div>;
        default:
            return "";
    }
};


const StatusBadge = ({status}) => {
    return <div className={classnames("Matrix_StatusBadge", status)}>{status}</div>;
}

class DeploymentInfo extends React.Component {

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
            buildTimestamp,
            lockStatus
        } = systemEnv;

        if (!ok) {
            return (<>
                <div className="Matrix_system_environment_toolLine"><LockStatus value={lockStatus}/></div>
                <div className="Matrix_system_environment_content">
                    <span className="Matrix_system_environment_headline"><StatusBadge status={status}/></span>
                    <div className="Matrix_system_environment_warning">{message || "n/a"}</div>
                </div>
            </>);
        }

        return (<>
            <div className="Matrix_system_environment_toolLine"><LockStatus value={lockStatus}/></div>
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
