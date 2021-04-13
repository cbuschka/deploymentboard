import React from 'react';
import './Message.css';

export class Message extends React.Component {

    render() {
        const {title, text} = this.props;
        return <div className="Message">
            <div className="Message__title">{title || ""}</div>
            <div className="Message__text">{text || ""}</div>
        </div>;
    }

}
