import React from 'react';
import {Button, Card, CardTitle, Col, Container, Form, FormGroup, Input, Label, Row} from "reactstrap";
import './LoginPage.css';

export class LoginPage extends React.Component {


    render() {
        return (<div className="LoginPage">
            <Container fluid>
                <Row>
                    <Col className="col-5  mx-auto">
                        <Card className="LoginPage_signin">
                            <CardTitle tag="h5" className="text-center">Login</CardTitle>
                            <Form>
                                <FormGroup>
                                    <Label htmlFor="inputUsername">Username</Label>
                                    <Input type="text" id="inputUsername" className="form-control"
                                           placeholder="e.g. hmeiser"
                                           required autoFocus/>
                                </FormGroup>

                                <FormGroup>
                                    <Label htmlFor="inputPassword">Password</Label>
                                    <Input type="password" id="inputPassword" className="form-control"
                                           placeholder="e.g. My passwooord is very strong!" required/>
                                </FormGroup>

                                <Button color="primary" block>Submit</Button>
                            </Form>
                        </Card>
                    </Col>
                </Row>
            </Container>
        </div>);
    }
}