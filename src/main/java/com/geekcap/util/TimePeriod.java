package com.geekcap.util;

import java.text.*;
import java.util.*;

public class TimePeriod
{
    private long timePeriod;
    private int days;
    private int hours;
    private int minutes;
    private int seconds;
    private String toString;

    public TimePeriod( long timePeriod )
    {
        this.timePeriod = timePeriod;
        init();
    }

    public TimePeriod( Date start, Date end )
    {
        this.timePeriod = end.getTime() - start.getTime();
        init();
    }

    private void init()
    {
        // Compute Days
        this.days = ( int )( this.timePeriod / 86400000 );
        
        // Compute Hours
        long timeWithoutDaysMs = this.timePeriod -
                                 ( this.days * 86400000 );
        this.hours = ( int )( timeWithoutDaysMs / 3600000 );

        // Compute Minutes
        long timeWithoutDaysHoursMs = this.timePeriod -
                                      ( this.days * 86400000 ) -
                                      ( this.hours * 3600000 );
        this.minutes = ( int )( timeWithoutDaysHoursMs / 60000 );

        // Compute seconds
        long timeWithoutDaysHoursMinutesMs = this.timePeriod -
                                             ( this.days * 86400000 ) -
                                             ( this.hours * 3600000 ) -
                                             ( this.minutes * 60000 );
        this.seconds = ( int )( timeWithoutDaysHoursMinutesMs / 1000 );

        // Build a string representation of our time period
        StringBuffer sb = new StringBuffer();
        if( this.days > 0 )
        {
            sb.append( this.days + " days, " );
        }
        if( this.days > 0 || this.hours > 0 )
        {
            sb.append( Integer.toString( this.hours ) );
            if( this.hours == 1 )
                sb.append( " hour, " );
            else
                sb.append( " hours, " );
        }
        if( this.days > 0 || this.hours > 0 || this.minutes > 0 )
        {
            sb.append( Integer.toString( this.minutes) );
            if( this.minutes == 1 )
                sb.append( " minute, " );
            else
                sb.append( " minutes, " );
        }
        if( this.days > 0 || this.hours > 0 || this.minutes > 0 || this.seconds > 0 )
        {
            sb.append( Integer.toString( this.seconds ) );
            if( this.seconds == 1 )
                sb.append( " second" );
            else
                sb.append( " seconds" );
        }
        this.toString = sb.toString();
    }

    public int getDays()
    {
        return this.days;
    }

    public int getHours()
    {
        return this.hours;
    }
    
    public int getMinutes()
    {
        return this.minutes;
    }

    public int getSeconds()
    {
        return this.seconds;
    }

    public boolean isLessThanHours( int hours )
    {
        if( this.days > 0 ||
            this.hours >= hours )
        {
            return false;
        }
        return true;
    }

    public boolean isLessThanMinutes( int minutes )
    {
        if( this.days > 0 ||
            this.hours > 0 ||
            this.minutes >= minutes )
        {
            return false;
        }
        return true;
    }
    
    public boolean isLessThanSeconds( int seconds )
    {
        if( this.days > 0 ||
            this.hours > 0 ||
            this.minutes > 0 ||
            this.seconds >= seconds )
        {
            return false;
        }
        return true;
    }

    public String toString()
    {
        return this.toString;
    }
}
