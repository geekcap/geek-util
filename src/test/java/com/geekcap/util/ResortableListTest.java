package com.geekcap.util;

import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class ResortableListTest
{
    private ResortableList<TestObject> testList;
    
    @Before
    public void setup()
    {
        testList = new ResortableList<TestObject>( TestObject.class, "myInt" );
        testList.add( new TestObject( 1, "abc", false, 4.0f ) );
        testList.add( new TestObject( 7, "cba", false, 6.0f ) );
        testList.add( new TestObject( 2, "bba", false, 3.0f ) );
        testList.add( new TestObject( 5, "ccd", false, 5.0f ) );
        testList.add( new TestObject( 3, "d", true, 2.0f ) );
        testList.add( new TestObject( 6, "a", true, 7.0f ) );
        testList.add( new TestObject( 4, "b", true, 1.0f ) );
    }
    
    @Test
    public void testGetSortableProperties()
    {
        // Load the object properties
        Set<String> props = testList.getSortablePropertyNames();
        
        // Assert that they are there
        Assert.assertTrue( "ResortableList is missing the myInt property.", props.contains( "myInt" ) );
        Assert.assertTrue( "ResortableList is missing the myString property.", props.contains( "myString" ) );
        Assert.assertTrue( "ResortableList is missing the myBoolean property.", props.contains( "myBoolean" ) );
        Assert.assertTrue( "ResortableList is missing the myFloat property.", props.contains( "myFloat" ) );
        
        // Make sure that we have exactly 4 entries
        Assert.assertEquals( "The ResortableList did not find four sortable properties on our test object", 4, props.size() );
    }
    
    @Test
    public void testSortInt()
    {
        // Sort the list
        testList.sort();
        
        // Just make sure that we don't corrupt the list with our sorting
        Assert.assertEquals( "We don't have the correct number of objects", 7, testList.size() );

        // Test the results
        int value = 1;
        for( TestObject o : testList )
        {
            Assert.assertEquals( "Integer value is not in order", value++, o.getMyInt() );
        }
    }
    
    @Test
    public void testSortIntDescending()
    {
        // Sort the list
        testList.sortDescending();
        
        // Just make sure that we don't corrupt the list with our sorting
        Assert.assertEquals( "We don't have the correct number of objects", 7, testList.size() );

        // Test the results
        int value = 7;
        for( TestObject o : testList )
        {
            Assert.assertEquals( "Integer value is not in order", value--, o.getMyInt() );
        }
    }


    @Test
    public void testSortString()
    {
        // Build our test results
        String[] results = new String[] { "a", "abc", "b", "bba", "cba", "ccd", "d" };
        
        // Sort the list by the myString property
        testList.setSortableProperty( "myString" );
        testList.sort();
        
        // Just make sure that we don't corrupt the list with our sorting
        Assert.assertEquals( "We don't have the correct number of objects", 7, testList.size() );
        
        // Test the results
        int index = 0;
        for( TestObject o : testList )
        {
            Assert.assertEquals( "String value is not in order", results[ index++ ], o.getMyString() );
        }
    }
    
    @Test
    public void testSortBoolean()
    {
        // Build our test results
        String[] results = new String[] { "a", "abc", "b", "bba", "cba", "ccd", "d" };
        
        // Sort the list by the myBoolean property
        testList.setSortableProperty( "myBoolean" );
        testList.sort();
        
        // Just make sure that we don't corrupt the list with our sorting
        Assert.assertEquals( "We don't have the correct number of objects", 7, testList.size() );
        
        // Test the results
        int index = 0;
        for( TestObject o : testList )
        {
            if( index++ < 4 )
                Assert.assertFalse( "MyBoolean should be false", o.isMyBoolean() );
            else
                Assert.assertTrue( "MyBoolean should be true", o.isMyBoolean() );
        }
    }

    @Test
    public void testSortFloat()
    {
        // Build our test results
        float[] results = new float[] { 1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f, 7.0f };
        
        // Sort the list by the myFloat property
        testList.setSortableProperty( "myFloat" );
        testList.sort();
        
        // Just make sure that we don't corrupt the list with our sorting
        Assert.assertEquals( "We don't have the correct number of objects", 7, testList.size() );
        
        // Test the results
        int index = 0;
        for( TestObject o : testList )
        {
            Assert.assertEquals( "Float value is not in order", results[ index++ ], o.getMyFloat() );
        }
    }

}

@Ignore
class TestObject
{
    private int myInt;
    private String myString;
    private boolean myBoolean;
    private float myFloat;
    
    public TestObject( int myInt, String myString, boolean myBoolean, float myFloat )
    {
        this.myInt = myInt;
        this.myString = myString;
        this.myBoolean = myBoolean;
        this.myFloat = myFloat;
    }

    /*
    public TestObject()
    {
    }
    */
    
    public int getMyInt()
    {
        return myInt;
    }
    
    public void setMyInt( int myInt )
    {
        this.myInt = myInt;
    }
    
    public String getMyString()
    {
        return myString;
    }
    
    public void setMyString( String myString )
    {
        this.myString = myString;
    }
    
    public boolean isMyBoolean()
    {
        return myBoolean;
    }
    
    public void setMyBoolean( boolean myBoolean )
    {
        this.myBoolean = myBoolean;
    }
    
    public float getMyFloat()
    {
        return myFloat;
    }
    
    public void setMyFloat( float myFloat )
    {
        this.myFloat = myFloat;
    }
    
    public String toString()
    {
        return myInt + ", " + myString + ", " + myBoolean + ", " + myFloat;
    }
    
    
}
