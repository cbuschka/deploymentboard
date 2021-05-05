import React from 'react';
import './InfoBlock.css';

export const InfoBlock = ({text}) => {
    if (!text) {
        return null;
    }

    return <span className="InfoBlock">{text}</span>;
};
