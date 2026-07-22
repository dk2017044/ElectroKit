import math
import random
from kivy.app import App
from kivy.uix.boxlayout import BoxLayout
from kivy.uix.gridlayout import GridLayout
from kivy.uix.floatlayout import FloatLayout
from kivy.uix.textinput import TextInput
from kivy.uix.button import Button
from kivy.uix.label import Label
from kivy.clock import Clock
from kivy.graphics import Color, Line, Rectangle, Ellipse, InstructionGroup
from kivy.core.window import Window
from kivy.metrics import dp

# Set window size to match standard flagship Android aspect ratio for simulation
Window.size = (400, 800)

class ProcessorAnimation(InstructionGroup):
    """
    Subtle processor-style circuit lines in top greeting background.
    Teal (#00BFA5) and Light Blue (#64B5F6) lines moving slowly.
    """
    def __init__(self, x, y, w, h, **kwargs):
        super().__init__(**kwargs)
        self.x = x
        self.y = y
        self.w = w
        self.h = h
        self.time = 0
        
        # Define some static circuit trace lines
        self.traces = [
            # [(start_x_pct, start_y_pct), (mid_x_pct, mid_y_pct), (end_x_pct, end_y_pct), color]
            [(0.1, 0.2), (0.3, 0.5), (0.5, 0.5), Color(0.0, 0.75, 0.65, 0.3)],
            [(0.9, 0.8), (0.7, 0.5), (0.5, 0.5), Color(0.39, 0.71, 0.96, 0.3)],
            [(0.2, 0.8), (0.4, 0.8), (0.6, 0.6), Color(0.0, 0.75, 0.65, 0.3)],
            [(0.8, 0.2), (0.6, 0.2), (0.4, 0.4), Color(0.39, 0.71, 0.96, 0.3)],
        ]
        
    def update(self, dt):
        self.clear()
        self.time += dt
        
        # Add background processor visual traces
        for trace in self.traces:
            p1, p2, p3, base_color = trace
            # Calculate absolute coords
            ax1, ay1 = self.x + p1[0]*self.w, self.y + p1[1]*self.h
            ax2, ay2 = self.x + p2[0]*self.w, self.y + p2[1]*self.h
            ax3, ay3 = self.x + p3[0]*self.w, self.y + p3[1]*self.h
            
            # Pulse opacity slowly (4-5s cycle)
            pulse = 0.25 + 0.1 * math.sin(self.time * (2 * math.pi / 4.5))
            self.add(Color(base_color.r, base_color.g, base_color.b, pulse))
            self.add(Line(points=[ax1, ay1, ax2, ay2, ax3, ay3], width=dp(1.5)))
            
            # Draw moving electron/current signal dot along paths
            dot_progress = (self.time * 0.2) % 1.0
            if dot_progress < 0.5:
                # Segment 1
                segment_pct = dot_progress / 0.5
                dx = ax1 + (ax2 - ax1) * segment_pct
                dy = ay1 + (ay2 - ay1) * segment_pct
            else:
                # Segment 2
                segment_pct = (dot_progress - 0.5) / 0.5
                dx = ax2 + (ax3 - ax2) * segment_pct
                dy = ay2 + (ay3 - ay2) * segment_pct
                
            self.add(Color(1, 1, 1, pulse * 1.5))
            self.add(Ellipse(pos=(dx - dp(3), dy - dp(3)), size=(dp(6), dp(6))))

class SearchBarGlow(InstructionGroup):
    """
    Search bar outline highlighted with rotating gradient line animation.
    Soft cyan (#00BCD4) and white rotating around the bar boundary.
    """
    def __init__(self, x, y, w, h, **kwargs):
        super().__init__(**kwargs)
        self.x = x
        self.y = y
        self.w = w
        self.h = h
        self.angle = 0
        
    def update(self, dt):
        self.clear()
        self.angle = (self.angle + dt * 120) % 360  # Rotate 360 deg every 3 seconds
        
        # Draw soft cyan neon glow outline
        self.add(Color(0.0, 0.74, 0.83, 0.6))
        # Draw rounded outline representing glowing gradient
        self.add(Line(rounded_rect=(self.x - dp(1), self.y - dp(1), self.w + dp(2), self.h + dp(2), dp(18)), width=dp(2)))
        
        # Add rotating white/bright cyan highlight indicator dot
        r_angle = math.radians(self.angle)
        cx, cy = self.x + self.w/2, self.y + self.h/2
        rx = self.w / 2
        ry = self.h / 2
        # Position tracing ellipse boundary approximately
        dot_x = cx + rx * math.cos(r_angle)
        dot_y = cy + ry * math.sin(r_angle)
        
        self.add(Color(1, 1, 1, 0.9))
        self.add(Ellipse(pos=(dot_x - dp(4), dot_y - dp(4)), size=(dp(8), dp(8))))

class ToolCardAnimation(InstructionGroup):
    """
    Modular tool card animators drawing functions in background.
    """
    def __init__(self, x, y, w, h, tool_type, **kwargs):
        super().__init__(**kwargs)
        self.x = x
        self.y = y
        self.w = w
        self.h = h
        self.tool_type = tool_type
        self.time = 0
        self.active = False
        
    def update(self, dt):
        self.clear()
        if not self.active:
            return
            
        self.time += dt
        
        if self.tool_type == "ohms_law":
            # Thin blue sine wave oscillation representing AC voltage/current
            self.add(Color(0.14, 0.38, 0.92, 0.3))
            points = []
            cycle_time = self.time * 4.0
            for i in range(0, int(self.w), 4):
                px = self.x + i
                py = self.y + self.h/2 + math.sin((i / self.w * 2 * math.pi) + cycle_time) * (self.h * 0.25)
                points.extend([px, py])
            if len(points) >= 4:
                self.add(Line(points=points, width=dp(1.5)))
                
        elif self.tool_type == "components_db":
            # Blueprint grid pattern pulsing grey-blue
            pulse = 0.15 + 0.1 * math.sin(self.time * math.pi)
            self.add(Color(0.4, 0.5, 0.6, pulse))
            # Draw vertical grid lines
            for i in range(1, 4):
                gx = self.x + (self.w / 4) * i
                self.add(Line(points=[gx, self.y, gx, self.y + self.h], width=dp(1)))
            # Draw horizontal grid lines
            for i in range(1, 4):
                gy = self.y + (self.h / 4) * i
                self.add(Line(points=[self.x, gy, self.x + self.w, gy], width=dp(1)))
                
        elif self.tool_type == "led_resistor":
            # Blinking soft yellow/green indicator dots
            pulse1 = 0.3 + 0.25 * math.sin(self.time * 3.0)
            pulse2 = 0.3 + 0.25 * math.cos(self.time * 3.0)
            
            # Yellow LED dot
            self.add(Color(1.0, 0.84, 0.0, pulse1))
            self.add(Ellipse(pos=(self.x + self.w*0.3 - dp(4), self.y + self.h*0.5 - dp(4)), size=(dp(8), dp(8))))
            
            # Lime LED dot
            self.add(Color(0.54, 0.98, 0.09, pulse2))
            self.add(Ellipse(pos=(self.x + self.w*0.7 - dp(4), self.y + self.h*0.5 - dp(4)), size=(dp(8), dp(8))))
            
        elif self.tool_type == "resistor_color":
            # Shifting diagonal gradient bands
            shift = (self.time * 50) % self.w
            self.add(Color(1.0, 0.5, 0.0, 0.2)) # Soft amber/red glow
            self.add(Line(points=[self.x + shift, self.y, self.x + shift + dp(10), self.y + self.h], width=dp(6)))
            
        elif self.tool_type == "series_parallel":
            # Cyan dotted current path traveling slowly
            self.add(Color(0.0, 0.9, 1.0, 0.3))
            dot_progress = (self.time * 0.3) % 1.0
            cx = self.x + self.w * dot_progress
            self.add(Ellipse(pos=(cx - dp(3), self.y + self.h*0.5 - dp(3)), size=(dp(6), dp(6))))
            
        elif self.tool_type == "number_system":
            # Binary fading random digits (0 / 1) representation
            random.seed(42)
            pulse = 0.2 + 0.15 * math.sin(self.time * 2.0)
            self.add(Color(1, 1, 1, pulse))
            # Just simple dot matrices indicating binary blocks
            for _ in range(5):
                rx = self.x + random.uniform(0.1, 0.9) * self.w
                ry = self.y + random.uniform(0.1, 0.9) * self.h
                self.add(Ellipse(pos=(rx, ry), size=(dp(4), dp(4))))

class ElectroKitApp(App):
    def build(self):
        # Root widget (vertical structure)
        self.root = BoxLayout(orientation='vertical', spacing=0)
        self.root.canvas.before.add(Color(0.05, 0.07, 0.1, 1.0)) # Flagship Slate Dark Background
        self.root.canvas.before.add(Rectangle(pos=(0, 0), size=(Window.width, Window.height)))
        
        # 1. Top greeting section
        greeting_box = FloatLayout(size_hint_y=0.18)
        
        # Greeting Texts
        lbl_welcome = Label(
            text="Hello, Engineer 👋",
            font_size='22sp',
            bold=True,
            color=(1, 1, 1, 1),
            pos_hint={'x': 0.06, 'y': 0.45},
            size_hint=(None, None),
            halign='left'
        )
        lbl_sub = Label(
            text="Welcome to ElectroKit Offline Toolkit",
            font_size='12sp',
            color=(0.7, 0.75, 0.8, 0.8),
            pos_hint={'x': 0.06, 'y': 0.15},
            size_hint=(None, None),
            halign='left'
        )
        greeting_box.add_widget(lbl_welcome)
        greeting_box.add_widget(lbl_sub)
        
        # 2. Search bar
        search_box = FloatLayout(size_hint_y=0.1)
        search_input = TextInput(
            text='',
            hint_text="Search any tool, calculator or component...",
            hint_text_color=(0.5, 0.55, 0.6, 0.7),
            background_color=(0.1, 0.12, 0.18, 0.85),
            foreground_color=(1, 1, 1, 1),
            multiline=False,
            font_size='13sp',
            padding=[dp(12), dp(10), dp(12), dp(10)],
            pos_hint={'center_x': 0.5, 'center_y': 0.5},
            size_hint=(0.88, 0.8)
        )
        search_box.add_widget(search_input)
        
        # 3. Horizontal Category filter Chips
        categories_layout = BoxLayout(orientation='horizontal', size_hint_y=0.08, padding=[dp(16), dp(4), dp(16), dp(4)], spacing=dp(8))
        categories = ["All", "Calculators", "Components", "Converters", "Favorite"]
        for cat in categories:
            btn_cat = Button(
                text=cat,
                font_size='11sp',
                background_normal='',
                background_color=(0.15, 0.2, 0.3, 0.9) if cat != "All" else (0.14, 0.38, 0.92, 1.0),
                color=(1, 1, 1, 1),
                size_hint_x=None,
                width=dp(76)
            )
            categories_layout.add_widget(btn_cat)
            
        # 4. Tool Cards Grid Section
        cards_grid = GridLayout(cols=2, spacing=dp(12), padding=dp(16), size_hint_y=0.54)
        
        # Tool card properties (title, subtext, animation type)
        self.tool_cards = [
            ("Ohm's Law", "Voltage, Current & Resistance", "ohms_law"),
            ("Components DB", "Datasheets & Pinouts Specs", "components_db"),
            ("LED Resistor", "Current Limiting & Power", "led_resistor"),
            ("Resistor Color", "4 & 5 Band Resistor Decoder", "resistor_color"),
            ("Series/Parallel", "Resistor & Capacitor Network", "series_parallel"),
            ("Number System", "Binary & Hex Base Converter", "number_system")
        ]
        
        self.animators = []
        
        for title, subtitle, anim_type in self.tool_cards:
            # Card FloatLayout
            card_layout = FloatLayout()
            btn_card = Button(
                background_normal='',
                background_color=(0.08, 0.1, 0.15, 0.85), # Glassmorphic Translucent Dark Gray
                size_hint=(1, 1),
                pos_hint={'x': 0, 'y': 0}
            )
            
            lbl_title = Label(
                text=title,
                font_size='14sp',
                bold=True,
                color=(1, 1, 1, 1),
                pos_hint={'x': 0.1, 'y': 0.55},
                size_hint=(None, None)
            )
            
            lbl_desc = Label(
                text=subtitle,
                font_size='9sp',
                color=(0.6, 0.65, 0.7, 0.8),
                pos_hint={'x': 0.1, 'y': 0.25},
                size_hint=(None, None)
            )
            
            card_layout.add_widget(btn_card)
            card_layout.add_widget(lbl_title)
            card_layout.add_widget(lbl_desc)
            
            # Setup callback to activate animation on click (User interaction trigger)
            def on_card_click(instance, a_type=anim_type):
                for animator in self.animators:
                    if animator.tool_type == a_type:
                        animator.active = not animator.active
            
            btn_card.bind(on_press=on_card_click)
            
            cards_grid.add_widget(card_layout)
            
        # 5. Bottom Navigation Bar
        nav_bar = BoxLayout(orientation='horizontal', size_hint_y=0.1, spacing=dp(4))
        nav_bar.canvas.before.add(Color(0.08, 0.1, 0.15, 1.0))
        nav_bar.canvas.before.add(Rectangle(pos=(0, 0), size=(Window.width, dp(80))))
        
        nav_options = ["Home", "Components", "Favorites", "Settings"]
        for option in nav_options:
            btn_nav = Button(
                text=option,
                font_size='11sp',
                background_normal='',
                background_color=(0, 0, 0, 0),
                color=(0.14, 0.38, 0.92, 1.0) if option == "Home" else (0.5, 0.55, 0.6, 1.0)
            )
            nav_bar.add_widget(btn_nav)
            
        # Add sections to root layout
        self.root.add_widget(greeting_box)
        self.root.add_widget(search_box)
        self.root.add_widget(categories_layout)
        self.root.add_widget(cards_grid)
        self.root.add_widget(nav_bar)
        
        # Schedule the animations to render continuously after setup
        Clock.schedule_once(lambda dt: self.init_canvas_animations(greeting_box, search_input, cards_grid), 0.1)
        
        return self.root

    def init_canvas_animations(self, greeting_box, search_input, cards_grid):
        # 1. Greeting Box Background Circuit Animation
        proc_anim = ProcessorAnimation(greeting_box.x, greeting_box.y, greeting_box.width, greeting_box.height)
        greeting_box.canvas.before.add(proc_anim)
        self.animators.append(proc_anim)
        
        # 2. Search Bar Outline Rotating Glow Animation
        search_glow = SearchBarGlow(search_input.x, search_input.y, search_input.width, search_input.height)
        search_input.canvas.after.add(search_glow)
        self.animators.append(search_glow)
        
        # 3. Tool Cards Background Animators
        for index, child in enumerate(reversed(cards_grid.children)):
            anim_type = self.tool_cards[index][2]
            card_anim = ToolCardAnimation(child.x, child.y, child.width, child.height, anim_type)
            child.canvas.before.add(card_anim)
            self.animators.append(card_anim)
            
        # Start Clock updates for smooth animation cycles
        Clock.schedule_interval(self.update_animations, 1.0 / 60.0)

    def update_animations(self, dt):
        for animator in self.animators:
            animator.update(dt)

if __name__ == '__main__':
    ElectroKitApp().run()
