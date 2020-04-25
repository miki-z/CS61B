@SuppressWarnings("WeakerAccess")
public class Planet {

    public double xxPos;
    public double yyPos;
    public double xxVel;
    public double yyVel;
    public double mass;
    public String imgFileName;
    public static final double G = 6.67 * Math.pow(10, -11);

    public Planet(double xP, double yP, double xV,
                  double yV, double m, String img) {
        xxPos = xP;
        yyPos = yP;
        xxVel = xV;
        yyVel = yV;
        mass = m;
        imgFileName = img;
    }
    public Planet(Planet p) {
        xxPos = p.xxPos;
        yyPos = p.yyPos;
        xxVel = p.xxVel;
        yyVel = p.yyVel;
        mass = p.mass;
        imgFileName = p.imgFileName;
    }

    public double calcDistance(Planet b) {
        double dx = this.xxPos - b.xxPos;
        double dy = this.yyPos - b.yyPos;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public double calcForceExertedBy(Planet c) {
        double r = this.calcDistance(c);
        return G * this.mass * c.mass / (r * r);

    }

    public double calcForceExertedByX(Planet b) {
        double r = this.calcDistance(b);
        double dx = b.xxPos - this.xxPos;
        double force = this.calcForceExertedBy(b);
        return force * dx / r;
    }

    public double calcForceExertedByY(Planet b) {
        double r = this.calcDistance(b);
        double dy = b.yyPos - this.yyPos;
        double force = this.calcForceExertedBy(b);
        return force * dy / r;
    }

    public double calcNetForceExertedByX(Planet[] allPlanets) {
        double netForceX = 0;
        for (Planet planet : allPlanets) {
            if (!this.equals(planet)) {
                netForceX += this.calcForceExertedByX(planet);
            }
        }
        return netForceX;
    }

    public double calcNetForceExertedByY(Planet[] allPlanets) {
        double netForceY = 0;
        for (Planet planet : allPlanets) {
            if (!this.equals(planet)) {
                netForceY += this.calcForceExertedByY(planet);
            }
        }
        return netForceY;
    }

    public void update(double dt, double fX, double fY) {
        double netAX = fX / this.mass;
        double netAY = fY / this.mass;
        this.xxVel = this.xxVel + dt * netAX;
        this.yyVel = this.yyVel + dt * netAY;
        this.xxPos = this.xxPos + dt * this.xxVel;
        this.yyPos = this.yyPos + dt * this.yyVel;
    }

    public void draw() {
        StdDraw.picture(this.xxPos, this.yyPos, "images/" + this.imgFileName);
    }
}