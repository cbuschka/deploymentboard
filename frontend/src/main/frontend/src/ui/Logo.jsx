import React from 'react';
import './Logo.css';

export const Logo = ({withLabel, onClick}) => {

    return (<div className="Logo" onClick={onClick}>
        <div className="Logo__icon"/>
        {withLabel ? <div className="Logo__label">Deployment<br/>Dashboard</div> : ""}
    </div>);
}