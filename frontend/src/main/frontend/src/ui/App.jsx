import React from 'react';
import {Redirect, Route, Router, Switch} from "react-router-dom";
import {appHistory} from "./AppHistory";
import {DashboardPage} from "./dashboard/DashboardPage";
import {Nav, Navbar, NavLink} from "reactstrap";
import {Logo} from "./Logo";

class App extends React.Component {

    render() {
        return (
            <div className="App">
                <Navbar color="dark" dark>
                    <a href="/"><Logo/></a>
                    <Nav fill horizontal>
                        <NavLink disabled href="/app/login">Login</NavLink>
                    </Nav>
                </Navbar>

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
