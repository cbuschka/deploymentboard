import React from 'react';
import "./BottomBar.css";


export const BottomBar = ({version}) => {
    return (<div className="BottomBar">DeploymentBoard &bull; {version} &bull; <a
        href="https://github.com/cbuschka/deploymentboard" target="_blank"
        rel="noreferrer noopener">on github</a>
    </div>);
}
