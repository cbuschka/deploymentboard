import React from 'react';
import './Logo.css';

export const Logo = ({withLabel}) => {

    return (<div className="Logo">
        <div className="Logo__icon"/>
        {withLabel ? <div className="Logo__label">Deployment<br/>Dashboard</div> : ""}
    </div>);
}