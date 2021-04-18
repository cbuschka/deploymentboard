import React from 'react';
import "./AppBar.css";
import {Logo} from "./Logo";
import {appHistory} from "./AppHistory";
import {
    Button,
    Container,
    Dropdown,
    DropdownMenu,
    DropdownToggle,
    Form,
    FormGroup,
    Input,
    Nav,
    Navbar,
    NavbarBrand,
    NavItem,
    NavLink
} from "reactstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";


export class AppBar extends React.Component {

    constructor(props) {
        super(props);

        this.state = {collapsed: true}
    }


    render() {
        const {collapsed} = this.state;

        return (<Navbar className="AppBar" full>
            <Container fluid>
                <NavbarBrand><Logo withLabel/></NavbarBrand>
                <Nav className="flex-row justify-content-between ml-auto">
                    <NavItem className=" order-2 order-md-1">
                        <NavLink tag="a" href="#" title="settings"><FontAwesomeIcon
                            icon={["fa", "fa-cog", "fa-fw", "fa-lg"]}/></NavLink>
                    </NavItem>
                    <Dropdown className="order-1" isOpen={!collapsed} toggle={this._onToggled}>
                        <DropdownToggle disabled caret color="secondary" outline>Login</DropdownToggle>
                        <DropdownMenu right className="mt-2">
                            <li className="px-3 py-2">
                                <Form role="form">
                                    <FormGroup className="form-group">
                                        <Input id="emailInput" placeholder="Email"
                                               className="form-control form-control-sm" type="text"
                                               required=""/>
                                    </FormGroup>
                                    <FormGroup className="form-group">
                                        <Input id="passwordInput" placeholder="Password"
                                               className="form-control form-control-sm" type="text"
                                               required/>
                                    </FormGroup>
                                    <FormGroup className="form-group">
                                        <Button block color="primary">Login</Button>
                                    </FormGroup>
                                </Form>
                            </li>
                        </DropdownMenu>
                    </Dropdown>
                </Nav>
            </Container>
        </Navbar>);
    }

    _onClick = (ev) => {
        if (window.location.pathname !== "/app/dashboard") {
            appHistory.push("/app/dashboard");
        }
        ev.preventDefault();
    }

    _onToggled = (ev) => {
        const {collapsed} = this.state;
        this.setState({collapsed: !collapsed});
    }
}
