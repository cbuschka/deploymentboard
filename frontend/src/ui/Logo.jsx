import React from 'react';
import './Logo.css';
import classnames from 'classnames';

export const Logo = ({className, withLabel, onClick}) => {

    return (<div className={classnames("Logo", className)} onClick={onClick}>
        <div className="Logo__icon"/>
        {withLabel ? <div className="Logo__labelBox">
            <div className="Logo__label">Deployment<br/>Dashboard</div>
        </div> : ""}
    </div>);
}
