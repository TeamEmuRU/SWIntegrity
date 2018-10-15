
#define JAN 1
#define FEB 2
#define MAR 3

short getMonthlySales(int month) {...}

float calculateRevenueForQuarter(short quarterSold) {...}

int determineFirstQuarterRevenue() {

// Variable for sales revenue for the quarter
float quarterRevenue = 0.0f;

short JanSold = getMonthlySales(JAN); /* Get sales in January */
short FebSold = getMonthlySales(FEB); /* Get sales in February */
short MarSold = getMonthlySales(MAR); /* Get sales in March */

// Calculate quarterly total
short quarterSold = JanSold + FebSold + MarSold;

if (JanSold + FebSold > -127)
	char sold =  JanSold + FebSold;

// Calculate the total revenue for the quarter
quarterRevenue = calculateRevenueForQuarter(quarterSold);

saveFirstQuarterRevenue(quarterRevenue);

int i = 3000000000000;


return 0;
}
