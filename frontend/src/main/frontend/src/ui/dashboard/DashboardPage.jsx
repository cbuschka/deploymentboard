import React from 'react';
import './DashboardPage.css';
import {dashboardStore} from "./DashboardStore";
import {loadDashboardState} from "./LoadDashboardStateAction";
import {dispatcher} from "@cbuschka/flux";

export class DashboardPage extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            environments: {}
        };
    }

    componentDidMount() {
        dispatcher.addHandler(dashboardStore);
        dispatcher.subscribe(this._onChange);
        loadDashboardState();
    }

    componentWillUnmount() {
        dispatcher.unsubscribe(this._onChange);
        dispatcher.removeHandler(dashboardStore);
    }

    _onChange = ({data}) => {
        const {dashboard: {state}} = data;
        this.setState(state);
    };

    render() {

        const {environments} = this.state;
        const systemNames = [];
        const envNames = Object.keys(environments);
        Object.keys(environments).forEach(envName => {
            Object.keys(environments[envName]).forEach(systemName => {
                if (systemNames.indexOf(systemName) === -1) {
                    systemNames.push(systemName);
                }
            })
        })

        return (
            <div className="DashboardPage">
                <table className="DashboardPage_matrix">
                    <thead>
                    <tr>
                        <th className="DashboardPage_environment">&nbsp;</th>
                        {systemNames.map(systemName => {
                            return <th className="DashboardPage_system" key={systemName}>{systemName}</th>
                        })}
                    </tr>
                    </thead>
                    <tbody>
                    {envNames.map(envName => {
                        const env = environments[envName];

                        return <tr className="DashboardPage_environment" key={envName}>
                            <td>{envName}</td>
                            {systemNames.map(systemName => {
                                const systemEnv = env[systemName];

                                return <td className="DashboardPage_system_environment"
                                           key={systemName + "_" + envName}>{systemEnv.version}</td>
                            })}
                        </tr>
                    })}
                    </tbody>
                </table>
            </div>
        );
    }
}
