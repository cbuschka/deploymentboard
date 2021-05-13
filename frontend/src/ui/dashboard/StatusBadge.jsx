import React from 'react';
import './StatusBadge.css';
import classnames from 'classnames';

export const StatusBadge = ({status}) => {
    return <div className={classnames("StatusBadge", status)}>{status}</div>;
}
