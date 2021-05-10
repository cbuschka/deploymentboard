import React from 'react';
import {Redirect, Route, Router, Switch} from "react-router-dom";
import {appHistory} from "./AppHistory";
import {DashboardPage} from "./dashboard/DashboardPage";
import "./App.css";
import {AppBar} from "./AppBar";
import {appStore} from './AppStore';
import {dispatcher} from "@cbuschka/flux";
import {loadAppState} from "./LoadAppStateAction";
import {BottomBar} from "./BottomBar";
import {IssuesPage} from "./issues/IssuesPage";

class App extends React.Component {

    constructor(props) {
        super(props);
        this.state = {version: {}, features: {}};
    }

    componentDidMount() {
        dispatcher.addHandler(appStore);
        dispatcher.subscribe(this._onChange);
        this._reloadAppState();
    }

    componentWillUnmount() {
        dispatcher.unsubscribe(this._onChange);
        dispatcher.removeHandler(appStore);
    }

    _reloadAppState = () => {
        loadAppState()
            .finally(() => {
                this.reloadTimer = window.setTimeout(this._reloadAppState, 30 * 1000);
            });
    }

    _onChange = ({data}) => {
        const {app: {features, version}} = data;
        this.setState({features, version})
    };

    render() {
        const {version: {version}} = this.state;

        return (
            <div className="App">
                <AppBar/>
                <Router history={appHistory} useHistory={true}>
                    <Switch>
                        <Route path="/app/deployments" exact component={DashboardPage}/>
                        <Route path="/app/issues" exact component={IssuesPage}/>
                        <Redirect from="/" to="/app/deployments"/>
                    </Switch>
                </Router>
                <BottomBar version={version}/>
            </div>
        );
    }
}

export default App;
