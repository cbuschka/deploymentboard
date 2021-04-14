import React from 'react';
import './DashboardPage.css';
import {dashboardStore} from "./DashboardStore";
import {loadDashboardState} from "./LoadDashboardStateAction";
import {dispatcher} from "@cbuschka/flux";
import {Message} from "./Message";
import {Matrix} from "./Matrix";
import {Logo} from "../Logo";

const TopBar = () => {

    return <div className="DashboardPage__OverBar_topLeft">
        <a href="/"><Logo/></a>
    </div>
}

export class DashboardPage extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            ok: true,
            environments: {}
        };
    }

    componentDidMount() {
        dispatcher.addHandler(dashboardStore);
        dispatcher.subscribe(this._onChange);
        this._reloadDashboardState();
    }

    componentWillUnmount() {
        window.clearTimeout(this.reloadTimer);
        dispatcher.unsubscribe(this._onChange);
        dispatcher.removeHandler(dashboardStore);
    }

    _reloadDashboardState = () => {
        loadDashboardState()
            .finally(() => {
                this.reloadTimer = window.setTimeout(this._reloadDashboardState, 5000);
            });
    }

    _onChange = ({data}) => {
        const {dashboard: {state}} = data;
        this.setState(state);
    };

    render() {
        const {ok} = this.state;

        return <div className="DashboardPage">
            <TopBar/>
            {ok ? this.renderMatrix() : this.renderError()}
        </div>;
    }

    renderError() {
        const {message} = this.state;
        return <Message title="Sorry, loading failed." text={message || "Whoops!"}/>;
    }

    renderMatrix() {
        const {environments} = this.state;
        return <Matrix environments={environments}/>;
    }
}