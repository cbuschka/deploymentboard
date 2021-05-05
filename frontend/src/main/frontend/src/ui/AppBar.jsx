import React from 'react';
import "./AppBar.css";
import {Logo} from "./Logo";
import {appHistory} from "./AppHistory";
import {Container, Navbar, NavbarBrand} from "reactstrap";


export class AppBar extends React.Component {

    constructor(props) {
        super(props);

        this.state = {collapsed: true}
    }


    render() {
        return (<Navbar className="AppBar" full>
            <Container fluid>
                <NavbarBrand><Logo withLabel/></NavbarBrand>
            </Container>
        </Navbar>);
    }

    _onClick = (ev) => {
        if (window.location.pathname !== "/app/dashboard") {
            appHistory.push("/app/dashboard");
        }
        ev.preventDefault();
    }
}
