import React from 'react';
import {Redirect, Route, Router, Switch} from "react-router-dom";
import {appHistory} from "./AppHistory";
import {DashboardPage} from "./dashboard/DashboardPage";
import "./App.css";

class App extends React.Component {

    render() {
        return (
            <div className="App">
                <Router history={appHistory} useHistory={true}>
                    <Switch>
                        <Route path="/app/dashboard" exact component={DashboardPage}/>
                        <Redirect from="/" to="/app/dashboard"/>
                    </Switch>
                </Router>
            </div>
        );
    }
}

export default App;
