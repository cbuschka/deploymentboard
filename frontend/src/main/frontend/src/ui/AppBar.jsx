import React from 'react';
import "./AppBar.css";
import {appHistory} from "./AppHistory";
import {Nav, Navbar, NavbarBrand, NavItem, NavLink} from "reactstrap";
import {Logo} from "./Logo";


export class AppBar extends React.Component {

    constructor(props) {
        super(props);

        this.state = {collapsed: true}
    }


    render() {
        return (<div className="AppBar">
            <NavbarBrand><Logo withLabel/></NavbarBrand>
            <Nav className="AppBar__Nav" tabs>
                <NavItem className="AppBar__NavItem" active>
                    <NavLink className="AppBar__NavLink" active href="/app/deployments">Deployments</NavLink>
                </NavItem>
            </Nav>
        </div>);
    }

    _onClick = (ev) => {
        if (window.location.pathname !== "/app/deployments") {
            appHistory.push("/app/deployments");
        }
        ev.preventDefault();
    }
}
