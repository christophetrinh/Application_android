# Getting started with MoneyApps

### Introduction

Manage your money expense in a better way with MoneyApps!
This Android application enables user to track his expenses all around the world by scanning receipt. Simple and intuitive, Money App provides responsive data visualization by filtering annual/monthly/daily expenses. Additionally, it displays a table grouping all expenses by tag or category. The application is made with a particular attention to provides a simple, intuitive and efficient way to manage expenses.

### Implementation

We will use several library, for instance OCR library by [Mobile Vision](https://developers.google.com/vision/) in order to extract information on the receipt. Moreover, we give the possibility to the user to fill the form manually. We add also a location based service with a map that shows the expenses on each country. Additionnaly, the user can synchronize his a google account in order to retrieve or store the data expenses in a spreadsheet.

### Interface descritpion

We opt for an interface which provides several swipe tabs in order to make intuitive navigation and a better user experience.

A floatting menu in the bottom right hand corner permits to the user to add an expense :
- **Manually** : A new activity will appear with all field empty. Thus, the user can manually entered the corresponding data into each field.
- **Automatically** : A camera activity will appear in order to retrieve information directly from a reciept. Firslty, present a receipt in front of the camera, then the *date* and the *amount* should be deteteced and should appear on the display. When this two fields are detected and displayed, click on the button *capture done*. Then an activity will appear with the *date* and *amount* fields filled. We also implement a geolocalisation, in background process, which allows to detect your current location in order to fill the *place* field on the expense edit activity.

Information on each expense :  
| Field | Description |  
|---------|:-------------|  
| *Retail* | The name of the retail or company. |  
| *Date* | The date of the expense. |  
| *Place* | The city where purchase has been done. |  
| *Amount* | The amount of the expense. |  
| *Category* | The category of the expenses (alimentation, transport ...). |  
| *Tag* | The tag that match to a specific project (travel, study ...). |  

The application provides the following tabs :
- **Home** : This tab provides a simple sentence which resume the total amount spent today/month/year (choose your preference in tab settings). It gives also an overview of the percentage of amount spent depending on *Category* or *Tag* field (choose your preference in tab settings) through a pie chart.
- **Table** : It enables users to display all their expenses in a simple way. It gives the possibility to edit expense by a long press on it and refresh the table after any edit intervention by a swipe down. You can sort the table by clicking on the corresponding field on the title row.
- **Visualization** : Through this tab, you can easly see the amount spent by year and for each mounth. The bar chart on the bottom shows the total amount spent on each year and the line chart on the top displays the amount spent on each mounth for the selected year (by a click on the bar chart on a specific year).
- **Map** : This tab allows the user to have an overview of his expenses around the world. Different markers on the map give the total amount spent in each city where the user spent money.
- **Settings** : It permits to the user to choose his preference for the Home tab (total amount spent day/month/year and the pie chart settings). Moreover, the user can link a Google Drive account in order to retrieve or store his expenses on a selected spreadsheet.

## Example 

In order to test the application, you can add [this spreadsheet](https://docs.google.com/spreadsheets/d/1DkZDt4gOWuzHNH0oxkb8jx-i4B2_tQshF_dHFFPJHZc/edit?usp=sharing) to your Google Drive account by open it and click on *File* and *Add to My Drive*. To retrieve the data to the application, run the application, go on the tab *Settings* and click on *Synchronize to Google Drive*. Then, configure your Google account and select the spreadsheet given below called *MoneyApps* and choose *Retrieve the data*.

You can also try to add an expense manually or even automatically. We provide below a typical recepeit that you can scan with you device, you can directly scan it on a display, no need to print it.

[![Ticket-CB.png](https://s23.postimg.org/u121357bf/Ticket_CB.png)](https://postimg.org/image/5xb9euouf/)


## Credits

Please feel free to contact us if you have any problem.
Created by [Mario Viola](<mario_viola@hotmail.fr>) and [Christophe Trinh](<christophe.trinh.94@gmail.com>)