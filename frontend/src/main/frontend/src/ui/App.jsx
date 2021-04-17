import React from 'react';
import {Redirect, Route, Router, Switch} from "react-router-dom";
import {appHistory} from "./AppHistory";
import {DashboardPage} from "./dashboard/DashboardPage";
import "./App.css";
import {LoginPage} from "./login/LoginPage";
import {AppBar} from "./AppBar";

class App extends React.Component {

    render() {
        return (
            <div className="App">
                <AppBar/>
                <Router history={appHistory} useHistory={true}>
                    <Switch>
                        <Route path="/app/login" exact component={LoginPage}/>
                        <Route path="/app/dashboard" exact component={DashboardPage}/>
                        <Redirect from="/" to="/app/dashboard"/>
                    </Switch>
                </Router>
            </div>
        );
    }
}

export default App;
