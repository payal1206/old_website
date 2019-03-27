#include <stdio.h>
#include <stdlib.h>

int health = 101;

int main() {
	unsigned int barrels = 10;
	char buffer[32];

	unsigned int choice;

	printf("welcome to donkey kong simulator 2019\n");
	printf("try to kill mario\n");

	while (barrels > 0) {
		printf("\n-------------------------\n");
		printf("what do you want to do?\n");
		printf("(1) throw a barrel\n");
		printf("(2) tell me how many barrels i have\n");
		printf("(3) tell me how much health mario has\n");
		printf("(4) give up :)\n");

		printf("> ");
		fflush(stdout);

		// gets is so easy to use
		gets(buffer);
		sscanf(buffer, "%u", &choice);

		switch (choice) {
			case 1:
				barrels--;
				health -= 10;
				break;
			case 2:
				printf("i have %u barrels for you\n", barrels);
				break;
			case 3:
				printf("mario has %d health left\n", health);
				break;
			case 4:
				printf("haha, knew you weren't a real donkey kong\n");
				return 0;
			default:
				// stop those bad hackerz
				printf("YOU'RE NOT ALLOWED TO DO THAT!!!");
				return 1;
		}
	}

	printf("you're out of barrels, sorry :(\n");

	if (health <= 0) {
		printf("you killed mario and won!\n");
		printf("here's your flag: ");
		system("/bin/cat flag.txt");
	}
}
