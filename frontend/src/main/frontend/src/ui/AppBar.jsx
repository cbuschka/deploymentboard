import React from 'react';
import "./AppBar.css";
import {Logo} from "./Logo";
import {appHistory} from "./AppHistory";


export class AppBar extends React.Component {

    render() {
        return (<div className="AppBar">
            <Logo className="align-left AppBar__Logo" onClick={this._onClick}/>
        </div>);
    }

    _onClick = (ev) => {
        if (window.location.pathname !== "/app/dashboard") {
            appHistory.push("/app/dashboard");
        }
        ev.preventDefault();
    }
}